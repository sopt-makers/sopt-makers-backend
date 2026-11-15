package org.sopt.makers.domain.admin.app.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.app.*;
import org.sopt.makers.domain.admin.app.port.AppLectureRepositoryPort;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.attendance.SubAttendance;
import org.sopt.makers.domain.admin.lecture.*;
import org.sopt.makers.domain.admin.lecture.exception.LectureException;
import org.sopt.makers.domain.admin.user.UserActivity;
import org.sopt.makers.domain.admin.user.port.AdminUserActivityPort;

class AppLectureServiceTest {
  private final AppLectureRepositoryPort lectures = mock(AppLectureRepositoryPort.class);
  private final AdminUserActivityPort members = mock(AdminUserActivityPort.class);
  private static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 15, 15, 59);

  private AppLectureService service(LocalDateTime now) {
    return new AppLectureService(
        lectures,
        members,
        39,
        Clock.fixed(now.toInstant(ZoneOffset.ofHours(9)), ZoneId.of("Asia/Seoul")));
  }

  private void member() {
    when(members.findActivityByUserIdAndGeneration(10L, 39))
        .thenReturn(Optional.of(new UserActivity(10L, "홍길동", 39, Part.SERVER, 2.0f)));
  }

  private AppLecture lecture(
      long id, LectureAttribute attribute, LectureStatus status, List<SubAttendance> subs) {
    return new AppLecture(
        id, "강의실", "세션", NOW.withHour(14), NOW.withHour(18), attribute, status, subs);
  }

  @Test
  @DisplayName("운영 기수 회원이 아니면 오늘 세션이 없는 응답을 반환한다")
  void returnsEmptyForInactiveMember() {
    when(members.findActivityByUserIdAndGeneration(10L, 39)).thenReturn(Optional.empty());
    assertThat(service(NOW).getTodayLecture(10L)).isEqualTo(AppLectureResult.empty());
    verifyNoInteractions(lectures);
  }

  @ParameterizedTest
  @CsvSource({"15,59,1", "16,0,2"})
  @DisplayName("하루 두 세션은 16시를 기준으로 표시 대상을 전환한다")
  void switchesAtSixteen(int hour, int minute, long expectedId) {
    member();
    when(lectures.findTodayLectures(10L, 39, Part.SERVER))
        .thenReturn(
            List.of(
                lecture(1, LectureAttribute.SEMINAR, LectureStatus.BEFORE, List.of()),
                lecture(2, LectureAttribute.EVENT, LectureStatus.BEFORE, List.of())));
    assertThat(service(NOW.withHour(hour).withMinute(minute)).getTodayLecture(10L).lecture().id())
        .isEqualTo(expectedId);
  }

  @ParameterizedTest
  @CsvSource({"SEMINAR,''", "EVENT,'행사도 참여하고, 출석점수도 받고, 일석이조!'", "ETC,'출석 점수가 반영되지 않아요.'"})
  @DisplayName("세션 종류별 안내 문구와 출석 대상 여부를 반환한다")
  void returnsMessages(LectureAttribute attribute, String message) {
    member();
    when(lectures.findTodayLectures(10L, 39, Part.SERVER))
        .thenReturn(List.of(lecture(1, attribute, LectureStatus.BEFORE, List.of())));
    var result = service(NOW).getTodayLecture(10L);
    assertThat(result.message()).isEqualTo(message);
    assertThat(result.type())
        .isEqualTo(
            attribute == LectureAttribute.ETC
                ? AppLectureResponseType.NO_ATTENDANCE
                : AppLectureResponseType.HAS_ATTENDANCE);
    assertThat(result.attendances()).isEmpty();
  }

  @Test
  @DisplayName("오늘 배정된 세션이 없으면 빈 응답을 반환한다")
  void returnsEmptyWithoutLecture() {
    member();
    when(lectures.findTodayLectures(10L, 39, Part.SERVER)).thenReturn(List.of());
    assertThat(service(NOW).getTodayLecture(10L)).isEqualTo(AppLectureResult.empty());
  }

  @Test
  @DisplayName("오늘 배정된 세션이 두 개를 초과하면 조회에 실패한다")
  void rejectsTooManyLectures() {
    member();
    var lecture = lecture(1, LectureAttribute.SEMINAR, LectureStatus.BEFORE, List.of());
    when(lectures.findTodayLectures(10L, 39, Part.SERVER))
        .thenReturn(List.of(lecture, lecture, lecture));
    assertThatThrownBy(() -> service(NOW).getTodayLecture(10L))
        .isInstanceOf(LectureException.class);
  }

  @Test
  @DisplayName("2차 출석 중 미출석이면 이전 차수의 결과만 보여준다")
  void hidesCurrentPendingAttendance() {
    member();
    var first =
        new SubAttendance(
            1L, 1L, 1L, 1, NOW.minusHours(1), AttendanceStatus.ATTENDANCE, NOW.minusMinutes(50));
    var second =
        new SubAttendance(
            1L, 1L, 1L, 2, NOW.minusMinutes(1), AttendanceStatus.ABSENT, NOW.minusMinutes(1));
    when(lectures.findTodayLectures(10L, 39, Part.SERVER))
        .thenReturn(
            List.of(
                lecture(
                    1, LectureAttribute.SEMINAR, LectureStatus.SECOND, List.of(first, second))));
    assertThat(service(NOW).getTodayLecture(10L).attendances()).containsExactly(first);
  }

  @Test
  @DisplayName("1차 출석 제한 시간이 지나면 미출석 결과도 보여준다")
  void showsExpiredAbsence() {
    member();
    var sub =
        new SubAttendance(
            1L, 1L, 1L, 1, NOW.minusMinutes(11), AttendanceStatus.ABSENT, NOW.minusMinutes(11));
    when(lectures.findTodayLectures(10L, 39, Part.SERVER))
        .thenReturn(
            List.of(lecture(1, LectureAttribute.SEMINAR, LectureStatus.FIRST, List.of(sub))));
    assertThat(service(NOW).getTodayLecture(10L).attendances()).containsExactly(sub);
  }

  @Test
  @DisplayName("오늘 진행 중인 출석 회차의 ID를 반환한다")
  void returnsCurrentRound() {
    when(lectures.findById(1L))
        .thenReturn(
            Optional.of(lecture(1, LectureAttribute.SEMINAR, LectureStatus.FIRST, List.of())));
    var sub = new AppSubLecture(2L, 1, NOW.minusMinutes(1));
    when(lectures.findSubLectureByLectureIdAndRound(1L, 1)).thenReturn(Optional.of(sub));
    assertThat(service(NOW).getCurrentLectureRound(1L)).isEqualTo(sub);
  }

  @Test
  @DisplayName("이전 날짜의 세션은 출석 회차를 조회할 수 없다")
  void rejectsPastLecture() {
    when(lectures.findById(1L))
        .thenReturn(
            Optional.of(
                new AppLecture(
                    1L,
                    "강의실",
                    "이전 세션",
                    NOW.minusDays(1),
                    NOW.minusDays(1),
                    LectureAttribute.SEMINAR,
                    LectureStatus.FIRST,
                    List.of())));
    when(lectures.findSubLectureByLectureIdAndRound(1L, 1))
        .thenReturn(Optional.of(new AppSubLecture(2L, 1, NOW.minusMinutes(1))));
    assertThatThrownBy(() -> service(NOW).getCurrentLectureRound(1L))
        .isInstanceOf(LectureException.class);
  }

  @Test
  @DisplayName("출석 제한 시간이 지난 회차는 조회할 수 없다")
  void rejectsExpiredRound() {
    when(lectures.findById(1L))
        .thenReturn(
            Optional.of(lecture(1, LectureAttribute.SEMINAR, LectureStatus.SECOND, List.of())));
    when(lectures.findSubLectureByLectureIdAndRound(1L, 2))
        .thenReturn(Optional.of(new AppSubLecture(2L, 2, NOW.minusMinutes(11))));
    assertThatThrownBy(() -> service(NOW).getCurrentLectureRound(1L))
        .isInstanceOf(LectureException.class);
  }
}
