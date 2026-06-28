package org.sopt.makers.domain.admin.app.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.admin.app.AppLecture;
import org.sopt.makers.domain.admin.app.AppLectureResponseType;
import org.sopt.makers.domain.admin.app.AppLectureResult;
import org.sopt.makers.domain.admin.app.AppSubLecture;
import org.sopt.makers.domain.admin.app.AppUserActivity;
import org.sopt.makers.domain.admin.app.port.AppLectureRepositoryPort;
import org.sopt.makers.domain.admin.app.port.AppUserActivityPort;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.attendance.LectureAttribute;
import org.sopt.makers.domain.admin.attendance.LectureStatus;
import org.sopt.makers.domain.admin.attendance.SubAttendance;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceException;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceFailure;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppLectureService {

  private static final int SUB_LECTURE_MAX_ROUND = 2;
  private static final int MAX_LECTURE_COUNT = 2;
  private static final int HACKATHON_LECTURE_START_HOUR = 14;
  private static final int ATTENDANCE_MINUTES = 10;
  private static final String SEMINAR_MESSAGE = "세미나 출석을 진행해주세요";
  private static final String EVENT_MESSAGE = "행사 출석을 진행해주세요";
  private static final String ETC_MESSAGE = "별도 출석이 없는 세션입니다";

  private final AppLectureRepositoryPort appLectureRepositoryPort;
  private final AppUserActivityPort appUserActivityPort;

  public AppLectureResult getTodayLecture(Long userId) {
    AppUserActivity activity =
        appUserActivityPort
            .findCurrentActivity(userId)
            .orElseThrow(() -> new AttendanceException(AttendanceFailure.NOT_FOUND_ATTENDANCE));

    List<AppLecture> lectures =
        appLectureRepositoryPort.findTodayLectures(userId, activity.generation(), activity.part());
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
            .orElseThrow(() -> new AttendanceException(AttendanceFailure.NOT_FOUND_LECTURE));

    AppSubLecture subLecture =
        appLectureRepositoryPort
            .findSubLectureByLectureIdAndRound(lectureId, getCurrentRound(lecture))
            .orElseThrow(() -> new AttendanceException(AttendanceFailure.NOT_FOUND_SUB_ATTENDANCE));
    validateLectureRound(lecture, subLecture);
    return subLecture;
  }

  private void validateLectureCount(List<AppLecture> lectures) {
    if (lectures.size() > SUB_LECTURE_MAX_ROUND) {
      throw new AttendanceException(AttendanceFailure.INVALID_SESSION_COUNT);
    }
  }

  private AppLecture getNowLecture(List<AppLecture> lectures) {
    return lectures.get(isMultipleLecture(lectures.size()) ? 1 : 0);
  }

  private boolean isMultipleLecture(int lectureCount) {
    return LocalDateTime.now().getHour() >= HACKATHON_LECTURE_START_HOUR
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
      List<SubAttendance> attendances = lecture.isFirst() ? List.of() : List.of(current);
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
      throw new AttendanceException(AttendanceFailure.LECTURE_NOT_STARTED);
    }
    if (isAttendanceEnded(subLecture)) {
      throw new AttendanceException(AttendanceFailure.ATTENDANCE_ENDED);
    }
    if (lecture.isEnd()) {
      throw new AttendanceException(AttendanceFailure.LECTURE_ENDED);
    }
  }

  private void validateTodayLecture(AppLecture lecture) {
    LocalDate today = LocalDate.now();
    boolean isNotTodayLecture = !lecture.startAt().toLocalDate().equals(today);

    if (isNotTodayLecture) {
      throw new AttendanceException(AttendanceFailure.NOT_FOUND_TODAY_LECTURE);
    }
  }

  private boolean isAttendanceEnded(SubAttendance subAttendance) {
    return subAttendance.subLectureStartAt() != null
        && subAttendance
            .subLectureStartAt()
            .plusMinutes(ATTENDANCE_MINUTES)
            .isBefore(LocalDateTime.now());
  }

  private boolean isAttendanceEnded(AppSubLecture subLecture) {
    return subLecture.startAt() != null
        && subLecture.startAt().plusMinutes(ATTENDANCE_MINUTES).isBefore(LocalDateTime.now());
  }
}
