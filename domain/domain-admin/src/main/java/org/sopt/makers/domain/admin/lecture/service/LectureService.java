package org.sopt.makers.domain.admin.lecture.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.alarm.Alarm;
import org.sopt.makers.domain.admin.alarm.AlarmCategory;
import org.sopt.makers.domain.admin.alarm.AlarmContent;
import org.sopt.makers.domain.admin.alarm.AlarmTarget;
import org.sopt.makers.domain.admin.alarm.port.AlarmInstantSenderPort;
import org.sopt.makers.domain.admin.attendance.Attendance;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.attendance.port.AttendanceLecturePort;
import org.sopt.makers.domain.admin.attendance.port.SubAttendanceLecturePort;
import org.sopt.makers.domain.admin.attendance.port.AttendanceRepositoryPort;
import org.sopt.makers.domain.admin.lecture.AttendanceStatusSummary;
import org.sopt.makers.domain.admin.lecture.Lecture;
import org.sopt.makers.domain.admin.lecture.LectureAttribute;
import org.sopt.makers.domain.admin.lecture.LectureStatus;
import org.sopt.makers.domain.admin.lecture.SubLecture;
import org.sopt.makers.domain.admin.lecture.exception.LectureException;
import org.sopt.makers.domain.admin.lecture.exception.LectureFailure;
import org.sopt.makers.domain.admin.lecture.port.LectureRepositoryPort;
import org.sopt.makers.domain.admin.lecture.port.SubLecturePort;
import org.sopt.makers.domain.admin.user.port.AdminUserActivityPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureService {

  private static final int SUB_LECTURE_MAX_ROUND = 2;
  private static final float BASE_ATTENDANCE_SCORE = 2.0f;
  private static final String ALARM_TITLE_SUFFIX = " 출석점수 반영";
  private static final String ALARM_CONTENT = "출석점수가 새롭게 반영되었어요! 내 점수를 확인해 볼까요?";

  private final LectureRepositoryPort lectureRepositoryPort;
  private final SubLecturePort subLecturePort;
  private final AttendanceLecturePort attendanceLecturePort;
  private final SubAttendanceLecturePort subAttendanceLecturePort;
  private final AdminUserActivityPort adminUserActivityPort;
  private final AttendanceRepositoryPort attendanceRepositoryPort;
  private final AlarmInstantSenderPort alarmInstantSenderPort;

  @Transactional
  public Lecture createLecture(
      Part part,
      String name,
      int generation,
      String place,
      LocalDateTime startDate,
      LocalDateTime endDate,
      LectureAttribute attribute) {
    if (!startDate.isBefore(endDate)) {
      throw new LectureException(LectureFailure.INVALID_LECTURE_TIME);
    }
    Lecture lecture =
        lectureRepositoryPort.save(
            name, part, generation, place, startDate, endDate, attribute, LectureStatus.BEFORE);

    List<Integer> rounds =
        java.util.stream.IntStream.rangeClosed(1, SUB_LECTURE_MAX_ROUND).boxed().toList();
    subLecturePort.saveAll(lecture.id(), rounds);

    List<Long> userIds = adminUserActivityPort.findUserIdsByGenerationAndPart(generation, part);
    List<Long> attendanceIds = attendanceLecturePort.saveAllForUsers(lecture.id(), userIds);

    List<Long> subLectureIds =
        subLecturePort.findAllByLectureId(lecture.id()).stream().map(SubLecture::id).toList();
    subAttendanceLecturePort.saveAllForAttendances(attendanceIds, subLectureIds);

    return lectureRepositoryPort
        .findById(lecture.id())
        .orElseThrow(() -> new LectureException(LectureFailure.NOT_FOUND_LECTURE));
  }

  public List<Lecture> getLectures(int generation, Part part) {
    return lectureRepositoryPort.findAllByGenerationAndPart(generation, part);
  }

  public Lecture getLecture(Long lectureId) {
    return lectureRepositoryPort
        .findById(lectureId)
        .orElseThrow(() -> new LectureException(LectureFailure.NOT_FOUND_LECTURE));
  }

  @Transactional
  public SubLecture startSubLecture(Long lectureId, int round, String code) {
    Lecture lecture = getLecture(lectureId);
    if (lecture.isEnd()) {
      throw new LectureException(LectureFailure.LECTURE_ENDED);
    }
    if (round == 2 && lecture.isBefore()) {
      throw new LectureException(LectureFailure.FIRST_ATTENDANCE_NOT_STARTED);
    }
    SubLecture subLecture =
        lecture.subLectures().stream()
            .filter(sl -> sl.round() == round)
            .findFirst()
            .orElseThrow(() -> new LectureException(LectureFailure.NO_MATCHING_SUB_LECTURE_ROUND));

    LocalDateTime startAt = LocalDateTime.now();
    subLecturePort.updateCodeAndStartAt(subLecture.id(), code, startAt);

    LectureStatus newStatus = (round == 1) ? LectureStatus.FIRST : LectureStatus.SECOND;
    lectureRepositoryPort.updateStatus(lectureId, newStatus);

    return new SubLecture(
        subLecture.id(),
        subLecture.lectureId(),
        subLecture.attribute(),
        subLecture.generation(),
        subLecture.round(),
        startAt,
        code);
  }

  @Transactional
  public void endLecture(Long lectureId) {
    Lecture lecture = getLecture(lectureId);
    if (lecture.isNotYetToEnd()) {
      throw new LectureException(LectureFailure.LECTURE_NOT_YET_ENDED);
    }
    if (lecture.isEnd()) {
      throw new LectureException(LectureFailure.LECTURE_ENDED);
    }

    lectureRepositoryPort.updateStatus(lectureId, LectureStatus.END);

    List<Long> userIds = attendanceLecturePort.getUserIdsByLectureId(lectureId);
    Map<Long, Float> userScores = computeUserScores(userIds, lecture.generation());
    adminUserActivityPort.bulkUpdateAttendanceScores(lecture.generation(), userScores);

    sendAttendanceAlarm(lecture, userIds);
  }

  @Transactional
  public void deleteLecture(Long lectureId) {
    Lecture lecture = getLecture(lectureId);

    List<Long> userIds = List.of();
    if (lecture.isEnd()) {
      userIds = attendanceLecturePort.getUserIdsByLectureId(lectureId);
    }

    List<Long> subLectureIds =
        subLecturePort.findAllByLectureId(lectureId).stream().map(SubLecture::id).toList();

    subAttendanceLecturePort.deleteAllBySubLectureIds(subLectureIds);
    subLecturePort.deleteAllByLectureId(lectureId);
    attendanceLecturePort.deleteByLectureId(lectureId);
    lectureRepositoryPort.deleteById(lectureId);

    if (lecture.isEnd()) {
      Map<Long, Float> userScores = computeUserScores(userIds, lecture.generation());
      adminUserActivityPort.bulkUpdateAttendanceScores(lecture.generation(), userScores);
    }
  }

  private Map<Long, Float> computeUserScores(List<Long> userIds, int generation) {
    List<Attendance> allEndedAttendances =
        attendanceRepositoryPort.findAllEndedByUserIds(userIds, generation);
    Map<Long, List<Attendance>> attendancesByUser =
        allEndedAttendances.stream().collect(Collectors.groupingBy(Attendance::userId));
    return userIds.stream()
        .collect(
            Collectors.toMap(
                userId -> userId,
                userId -> {
                  List<Attendance> userAttendances =
                      attendancesByUser.getOrDefault(userId, List.of());
                  return BASE_ATTENDANCE_SCORE
                      + (float)
                          userAttendances.stream().mapToDouble(Attendance::computeScore).sum();
                }));
  }

  private void sendAttendanceAlarm(Lecture lecture, List<Long> userIds) {
    List<String> targetIds = userIds.stream().map(String::valueOf).toList();
    AlarmTarget target = AlarmTarget.partialForCsv(lecture.generation(), targetIds);
    AlarmContent content =
        AlarmContent.withoutLink(
            lecture.name() + ALARM_TITLE_SUFFIX, ALARM_CONTENT, AlarmCategory.NOTICE);
    alarmInstantSenderPort.send(Alarm.instant(target, content));
  }

  public Lecture getLectureDetail(Long lectureId) {
    return getLecture(lectureId);
  }

  public AttendanceStatusSummary getAttendanceSummary(Lecture lecture) {
    boolean isEnded = lecture.isEnd();
    int absentCount =
        attendanceLecturePort.countByLectureIdAndStatus(lecture.id(), AttendanceStatus.ABSENT);
    return new AttendanceStatusSummary(
        attendanceLecturePort.countByLectureIdAndStatus(
            lecture.id(), AttendanceStatus.ATTENDANCE),
        isEnded ? absentCount : 0,
        attendanceLecturePort.countByLectureIdAndStatus(lecture.id(), AttendanceStatus.TARDY),
        isEnded ? 0 : absentCount);
  }
}
