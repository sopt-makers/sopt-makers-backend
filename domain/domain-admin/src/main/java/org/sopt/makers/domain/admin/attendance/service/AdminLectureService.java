package org.sopt.makers.domain.admin.attendance.service;

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
import org.sopt.makers.domain.admin.attendance.AdminLecture;
import org.sopt.makers.domain.admin.attendance.Attendance;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.attendance.LectureAttribute;
import org.sopt.makers.domain.admin.attendance.LectureStatus;
import org.sopt.makers.domain.admin.attendance.SubLecture;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceException;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceFailure;
import org.sopt.makers.domain.admin.attendance.port.AdminAttendanceLecturePort;
import org.sopt.makers.domain.admin.attendance.port.AdminLectureRepositoryPort;
import org.sopt.makers.domain.admin.attendance.port.AdminLectureUserPort;
import org.sopt.makers.domain.admin.attendance.port.AdminSubAttendanceLecturePort;
import org.sopt.makers.domain.admin.attendance.port.AdminSubLectureLecturePort;
import org.sopt.makers.domain.admin.attendance.port.AttendanceRepositoryPort;
import org.sopt.makers.domain.admin.attendance.port.AttendanceUserActivityPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminLectureService {

  private static final int SUB_LECTURE_MAX_ROUND = 2;
  private static final float BASE_ATTENDANCE_SCORE = 2.0f;
  private static final String ALARM_TITLE_SUFFIX = " 출석점수 반영";
  private static final String ALARM_CONTENT = "출석점수가 새롭게 반영되었어요! 내 점수를 확인해 볼까요?";

  private final AdminLectureRepositoryPort adminLectureRepositoryPort;
  private final AdminSubLectureLecturePort adminSubLectureLecturePort;
  private final AdminAttendanceLecturePort adminAttendanceLecturePort;
  private final AdminSubAttendanceLecturePort adminSubAttendanceLecturePort;
  private final AdminLectureUserPort adminLectureUserPort;
  private final AttendanceRepositoryPort attendanceRepositoryPort;
  private final AttendanceUserActivityPort attendanceUserActivityPort;
  private final AlarmInstantSenderPort alarmInstantSenderPort;

  @Transactional
  public AdminLecture createLecture(
      Part part,
      String name,
      int generation,
      String place,
      LocalDateTime startDate,
      LocalDateTime endDate,
      LectureAttribute attribute) {
    AdminLecture lecture =
        adminLectureRepositoryPort.save(
            name, part, generation, place, startDate, endDate, attribute, LectureStatus.BEFORE);

    List<Integer> rounds =
        java.util.stream.IntStream.rangeClosed(1, SUB_LECTURE_MAX_ROUND).boxed().toList();
    adminSubLectureLecturePort.saveAll(lecture.id(), rounds);

    List<Long> userIds = adminLectureUserPort.findUserIdsByGenerationAndPart(generation, part);
    List<Long> attendanceIds = adminAttendanceLecturePort.saveAllForUsers(lecture.id(), userIds);

    List<Long> subLectureIds =
        adminSubLectureLecturePort.findAllByLectureId(lecture.id()).stream()
            .map(SubLecture::id)
            .toList();
    adminSubAttendanceLecturePort.saveAllForAttendances(attendanceIds, subLectureIds);

    return adminLectureRepositoryPort
        .findById(lecture.id())
        .orElseThrow(() -> new AttendanceException(AttendanceFailure.NOT_FOUND_LECTURE));
  }

  public List<AdminLecture> getLectures(int generation, Part part) {
    return adminLectureRepositoryPort.findAllByGenerationAndPart(generation, part);
  }

  public AdminLecture getLecture(Long lectureId) {
    return adminLectureRepositoryPort
        .findById(lectureId)
        .orElseThrow(() -> new AttendanceException(AttendanceFailure.NOT_FOUND_LECTURE));
  }

  @Transactional
  public SubLecture startSubLecture(Long lectureId, int round, String code) {
    AdminLecture lecture = getLecture(lectureId);
    if (lecture.isEnd()) {
      throw new AttendanceException(AttendanceFailure.LECTURE_ENDED);
    }
    if (round == 2 && lecture.isBefore()) {
      throw new AttendanceException(AttendanceFailure.FIRST_ATTENDANCE_NOT_STARTED);
    }
    SubLecture subLecture =
        lecture.subLectures().stream()
            .filter(sl -> sl.round() == round)
            .findFirst()
            .orElseThrow(
                () -> new AttendanceException(AttendanceFailure.NO_MATCHING_SUB_LECTURE_ROUND));

    LocalDateTime startAt = LocalDateTime.now();
    adminSubLectureLecturePort.updateCodeAndStartAt(subLecture.id(), code, startAt);

    LectureStatus newStatus = (round == 1) ? LectureStatus.FIRST : LectureStatus.SECOND;
    adminLectureRepositoryPort.updateStatus(lectureId, newStatus);

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
    AdminLecture lecture = getLecture(lectureId);
    if (lecture.isNotYetToEnd()) {
      throw new AttendanceException(AttendanceFailure.LECTURE_NOT_YET_ENDED);
    }
    if (lecture.isEnd()) {
      throw new AttendanceException(AttendanceFailure.LECTURE_ENDED);
    }

    adminLectureRepositoryPort.updateStatus(lectureId, LectureStatus.END);

    List<Long> userIds = adminAttendanceLecturePort.getUserIdsByLectureId(lectureId);
    Map<Long, Float> userScores = computeUserScores(userIds, lecture.generation());
    attendanceUserActivityPort.bulkUpdateAttendanceScores(lecture.generation(), userScores);

    sendAttendanceAlarm(lecture, userIds);
  }

  @Transactional
  public void deleteLecture(Long lectureId) {
    AdminLecture lecture = getLecture(lectureId);

    List<Long> userIds = List.of();
    if (lecture.isEnd()) {
      userIds = adminAttendanceLecturePort.getUserIdsByLectureId(lectureId);
    }

    List<Long> subLectureIds =
        adminSubLectureLecturePort.findAllByLectureId(lectureId).stream()
            .map(SubLecture::id)
            .toList();

    adminSubAttendanceLecturePort.deleteAllBySubLectureIds(subLectureIds);
    adminSubLectureLecturePort.deleteAllByLectureId(lectureId);
    adminAttendanceLecturePort.deleteByLectureId(lectureId);
    adminLectureRepositoryPort.deleteById(lectureId);

    if (lecture.isEnd()) {
      Map<Long, Float> userScores = computeUserScores(userIds, lecture.generation());
      attendanceUserActivityPort.bulkUpdateAttendanceScores(lecture.generation(), userScores);
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
                      + (float) userAttendances.stream()
                          .mapToDouble(Attendance::computeScore)
                          .sum();
                }));
  }

  private void sendAttendanceAlarm(AdminLecture lecture, List<Long> userIds) {
    List<String> targetIds = userIds.stream().map(String::valueOf).toList();
    AlarmTarget target = AlarmTarget.partialForCsv(lecture.generation(), targetIds);
    AlarmContent content =
        AlarmContent.withoutLink(
            lecture.name() + ALARM_TITLE_SUFFIX, ALARM_CONTENT, AlarmCategory.NOTICE);
    alarmInstantSenderPort.send(Alarm.instant(target, content));
  }

  public AdminLecture getLectureDetail(Long lectureId) {
    return getLecture(lectureId);
  }

  public int countAttendanceByStatus(Long lectureId, AttendanceStatus status) {
    return adminAttendanceLecturePort.countByLectureIdAndStatus(lectureId, status);
  }
}
