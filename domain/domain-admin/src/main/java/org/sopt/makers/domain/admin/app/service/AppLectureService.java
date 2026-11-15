package org.sopt.makers.domain.admin.app.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.admin.app.AppLecture;
import org.sopt.makers.domain.admin.app.AppLectureResponseType;
import org.sopt.makers.domain.admin.app.AppLectureResult;
import org.sopt.makers.domain.admin.app.AppSubLecture;
import org.sopt.makers.domain.admin.app.port.AppLectureRepositoryPort;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.attendance.SubAttendance;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceException;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceFailure;
import org.sopt.makers.domain.admin.lecture.LectureAttribute;
import org.sopt.makers.domain.admin.lecture.LectureStatus;
import org.sopt.makers.domain.admin.lecture.exception.LectureException;
import org.sopt.makers.domain.admin.lecture.exception.LectureFailure;
import org.sopt.makers.domain.admin.user.port.AdminUserActivityPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AppLectureService {

  private static final int SUB_LECTURE_MAX_ROUND = 2;
  private static final int MAX_LECTURE_COUNT = 2;
  private static final int HACKATHON_LECTURE_START_HOUR = 16;
  private static final int ATTENDANCE_MINUTES = 10;
  private static final String SEMINAR_MESSAGE = "";
  private static final String EVENT_MESSAGE = "행사도 참여하고, 출석점수도 받고, 일석이조!";
  private static final String ETC_MESSAGE = "출석 점수가 반영되지 않아요.";

  private final AppLectureRepositoryPort appLectureRepositoryPort;
  private final AdminUserActivityPort adminUserActivityPort;
  private final int currentGeneration;
  private final Clock clock;

  public AppLectureService(
      AppLectureRepositoryPort appLectureRepositoryPort,
      AdminUserActivityPort adminUserActivityPort,
      @Value("${sopt.current.generation}") int currentGeneration,
      Clock clock) {
    this.appLectureRepositoryPort = appLectureRepositoryPort;
    this.adminUserActivityPort = adminUserActivityPort;
    this.currentGeneration = currentGeneration;
    this.clock = clock;
  }

  public AppLectureResult getTodayLecture(Long userId) {
    var activity =
        adminUserActivityPort.findActivityByUserIdAndGeneration(userId, currentGeneration);
    if (activity.isEmpty()) return AppLectureResult.empty();
    List<AppLecture> lectures =
        appLectureRepositoryPort.findTodayLectures(
            userId, currentGeneration, activity.get().part());
    validateLectureCount(lectures);

    if (lectures.isEmpty()) {
      return AppLectureResult.empty();
    }

    AppLecture lecture = getNowLecture(lectures);
    AppLectureResponseType responseType = getResponseType(lecture);
    String message = getMessage(lecture.attribute());

    if (responseType == AppLectureResponseType.NO_ATTENDANCE || lecture.isBefore()) {
      return new AppLectureResult(responseType, lecture, message, List.of());
    }

    return getTodayLectureResult(lecture, responseType, message);
  }

  public AppSubLecture getCurrentLectureRound(Long lectureId) {
    AppLecture lecture =
        appLectureRepositoryPort
            .findById(lectureId)
            .orElseThrow(() -> new LectureException(LectureFailure.NOT_FOUND_LECTURE));

    AppSubLecture subLecture =
        appLectureRepositoryPort
            .findSubLectureByLectureIdAndRound(lectureId, getCurrentRound(lecture))
            .orElseThrow(() -> new AttendanceException(AttendanceFailure.NOT_FOUND_SUB_ATTENDANCE));
    validateLectureRound(lecture, subLecture);
    return subLecture;
  }

  private void validateLectureCount(List<AppLecture> lectures) {
    if (lectures.size() > MAX_LECTURE_COUNT) {
      throw new LectureException(LectureFailure.INVALID_SESSION_COUNT);
    }
  }

  private AppLecture getNowLecture(List<AppLecture> lectures) {
    return lectures.get(isMultipleLecture(lectures.size()) ? 1 : 0);
  }

  private boolean isMultipleLecture(int lectureCount) {
    return LocalDateTime.now(clock).getHour() >= HACKATHON_LECTURE_START_HOUR
        && lectureCount == MAX_LECTURE_COUNT;
  }

  private AppLectureResponseType getResponseType(AppLecture lecture) {
    return lecture.attribute() == LectureAttribute.ETC
        ? AppLectureResponseType.NO_ATTENDANCE
        : AppLectureResponseType.HAS_ATTENDANCE;
  }

  private String getMessage(LectureAttribute attribute) {
    return switch (attribute) {
      case SEMINAR -> SEMINAR_MESSAGE;
      case EVENT -> EVENT_MESSAGE;
      case ETC -> ETC_MESSAGE;
    };
  }

  private AppLectureResult getTodayLectureResult(
      AppLecture lecture, AppLectureResponseType responseType, String message) {
    SubAttendance current = getCurrentSubAttendance(lecture);

    if (isOnAttendanceAbsence(current)) {
      List<SubAttendance> attendances =
          lecture.subAttendances().stream().filter(sa -> sa.round() < current.round()).toList();
      return new AppLectureResult(responseType, lecture, message, attendances);
    }

    List<SubAttendance> attendances =
        lecture.isFirst() ? List.of(current) : lecture.subAttendances();
    return new AppLectureResult(responseType, lecture, message, attendances);
  }

  private SubAttendance getCurrentSubAttendance(AppLecture lecture) {
    int round = lecture.isFirst() ? 1 : 2;
    return lecture.subAttendances().stream()
        .filter(subAttendance -> subAttendance.round() == round)
        .findFirst()
        .orElseThrow(() -> new AttendanceException(AttendanceFailure.NOT_FOUND_SUB_ATTENDANCE));
  }

  private boolean isOnAttendanceAbsence(SubAttendance subAttendance) {
    return !isAttendanceEnded(subAttendance) && subAttendance.status() == AttendanceStatus.ABSENT;
  }

  private int getCurrentRound(AppLecture lecture) {
    return lecture.status() == LectureStatus.FIRST ? 1 : 2;
  }

  private void validateLectureRound(AppLecture lecture, AppSubLecture subLecture) {
    validateTodayLecture(lecture);
    if (lecture.isBefore()) {
      throw new LectureException(LectureFailure.LECTURE_NOT_STARTED);
    }
    if (isAttendanceEnded(subLecture)) {
      throw new LectureException(LectureFailure.ATTENDANCE_ENDED);
    }
    if (lecture.isEnd()) {
      throw new LectureException(LectureFailure.LECTURE_ENDED);
    }
  }

  private void validateTodayLecture(AppLecture lecture) {
    LocalDate today = LocalDate.now(clock);
    boolean isNotTodayLecture = !lecture.startAt().toLocalDate().equals(today);

    if (isNotTodayLecture) {
      throw new LectureException(LectureFailure.NOT_FOUND_TODAY_LECTURE);
    }
  }

  private boolean isAttendanceEnded(SubAttendance subAttendance) {
    return subAttendance.subLectureStartAt() != null
        && subAttendance
            .subLectureStartAt()
            .plusMinutes(ATTENDANCE_MINUTES)
            .isBefore(LocalDateTime.now(clock));
  }

  private boolean isAttendanceEnded(AppSubLecture subLecture) {
    return subLecture.startAt() != null
        && subLecture.startAt().plusMinutes(ATTENDANCE_MINUTES).isBefore(LocalDateTime.now(clock));
  }
}
