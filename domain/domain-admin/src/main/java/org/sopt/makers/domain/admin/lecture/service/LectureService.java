package org.sopt.makers.domain.admin.lecture.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.attendance.Attendance;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.attendance.port.AttendanceLecturePort;
import org.sopt.makers.domain.admin.attendance.port.AttendanceRepositoryPort;
import org.sopt.makers.domain.admin.attendance.port.SubAttendanceLecturePort;
import org.sopt.makers.domain.admin.lecture.AttendanceStatusSummary;
import org.sopt.makers.domain.admin.lecture.Lecture;
import org.sopt.makers.domain.admin.lecture.LectureAttribute;
import org.sopt.makers.domain.admin.lecture.LectureEndedEvent;
import org.sopt.makers.domain.admin.lecture.LectureStatus;
import org.sopt.makers.domain.admin.lecture.SubLecture;
import org.sopt.makers.domain.admin.lecture.exception.LectureException;
import org.sopt.makers.domain.admin.lecture.exception.LectureFailure;
import org.sopt.makers.domain.admin.lecture.port.LectureRepositoryPort;
import org.sopt.makers.domain.admin.lecture.port.SubLecturePort;
import org.sopt.makers.domain.admin.user.port.AdminUserActivityPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureService {

  private static final int SUB_LECTURE_MAX_ROUND = 2;

  private final LectureRepositoryPort lectureRepositoryPort;
  private final SubLecturePort subLecturePort;
  private final AttendanceLecturePort attendanceLecturePort;
  private final SubAttendanceLecturePort subAttendanceLecturePort;
  private final AdminUserActivityPort adminUserActivityPort;
  private final AttendanceRepositoryPort attendanceRepositoryPort;
  private final ApplicationEventPublisher eventPublisher;

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
    if (round == 1 && !lecture.isBefore()) {
      throw new LectureException(LectureFailure.ALREADY_STARTED_ROUND);
    }
    if (round == 2 && lecture.isBefore()) {
      throw new LectureException(LectureFailure.FIRST_ATTENDANCE_NOT_STARTED);
    }
    if (round == 2 && lecture.isSecond()) {
      throw new LectureException(LectureFailure.ALREADY_STARTED_ROUND);
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

    eventPublisher.publishEvent(new LectureEndedEvent(lecture, userIds));
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
                userId ->
                    Attendance.computeTotalScore(
                        attendancesByUser.getOrDefault(userId, List.of()))));
  }

  public Lecture getLectureDetail(Long lectureId) {
    return getLecture(lectureId);
  }

  public AttendanceStatusSummary getAttendanceSummary(Lecture lecture) {
    Map<Long, Map<AttendanceStatus, Integer>> countsMap =
        attendanceLecturePort.countByLectureIdsGroupByStatus(List.of(lecture.id()));
    return toSummary(lecture.isEnd(), countsMap.getOrDefault(lecture.id(), Map.of()));
  }

  public Map<Long, AttendanceStatusSummary> getAttendanceSummaries(List<Lecture> lectures) {
    List<Long> lectureIds = lectures.stream().map(Lecture::id).toList();
    Map<Long, Map<AttendanceStatus, Integer>> countsMap =
        attendanceLecturePort.countByLectureIdsGroupByStatus(lectureIds);
    return lectures.stream()
        .collect(
            Collectors.toMap(
                Lecture::id,
                lecture ->
                    toSummary(lecture.isEnd(), countsMap.getOrDefault(lecture.id(), Map.of()))));
  }

  private AttendanceStatusSummary toSummary(
      boolean isEnded, Map<AttendanceStatus, Integer> counts) {
    int absentCount = counts.getOrDefault(AttendanceStatus.ABSENT, 0);
    return new AttendanceStatusSummary(
        counts.getOrDefault(AttendanceStatus.ATTENDANCE, 0),
        isEnded ? absentCount : 0,
        counts.getOrDefault(AttendanceStatus.TARDY, 0),
        isEnded ? 0 : absentCount);
  }
}
