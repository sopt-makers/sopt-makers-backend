package org.sopt.makers.domain.admin.attendance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.attendance.Attendance;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.attendance.MemberAttendanceDetail;
import org.sopt.makers.domain.admin.attendance.SubAttendance;
import org.sopt.makers.domain.admin.attendance.UserAttendanceSummary;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceException;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceFailure;
import org.sopt.makers.domain.admin.attendance.port.AttendanceRepositoryPort;
import org.sopt.makers.domain.admin.attendance.port.SubAttendanceRepositoryPort;
import org.sopt.makers.domain.admin.lecture.LectureAttribute;
import org.sopt.makers.domain.admin.lecture.SubLecture;
import org.sopt.makers.domain.admin.lecture.exception.LectureException;
import org.sopt.makers.domain.admin.lecture.port.SubLectureRepositoryPort;
import org.sopt.makers.domain.admin.member.Member;
import org.sopt.makers.domain.admin.user.UserActivity;
import org.sopt.makers.domain.admin.user.port.AdminUserActivityPort;

class AttendanceServiceTest {

  private final SubLectureRepositoryPort subLecturePort = mock(SubLectureRepositoryPort.class);
  private final SubAttendanceRepositoryPort subAttendancePort =
      mock(SubAttendanceRepositoryPort.class);
  private final AttendanceRepositoryPort attendancePort = mock(AttendanceRepositoryPort.class);
  private final AdminUserActivityPort activityPort = mock(AdminUserActivityPort.class);
  private final AttendanceService service =
      new AttendanceService(subLecturePort, attendancePort, subAttendancePort, activityPort, 39);

  @Test
  @DisplayName("운영 기수의 회원 정보와 출석 상세를 함께 반환한다")
  void returnsActivityAndAttendanceDetails() {
    Long userId = 10L;
    Member activity = new Member(userId, "홍길동", 39, Part.SERVER, 1.5f, 0, 0, 0, 0);
    LocalDateTime start = LocalDateTime.of(2026, 10, 1, 14, 0);
    LocalDateTime end = start.plusHours(4);
    MemberAttendanceDetail detail =
        new MemberAttendanceDetail(
            "세미나",
            start,
            end,
            -0.5f,
            AttendanceStatus.TARDY,
            List.of(
                new MemberAttendanceDetail.SubAttendanceDetail(
                    1, AttendanceStatus.ATTENDANCE, start.plusMinutes(10))));
    when(activityPort.findMemberByUserIdAndGeneration(userId, 39))
        .thenReturn(Optional.of(activity));
    when(attendancePort.findAllDetailsByUserIdAndGeneration(userId, activity.generation()))
        .thenReturn(List.of(detail));

    UserAttendanceSummary result = service.getAttendancesByUserId(userId);

    assertThat(result.member()).isEqualTo(activity);
    assertThat(result.lectures()).containsExactly(detail);
    verify(attendancePort).findAllDetailsByUserIdAndGeneration(userId, 39);
  }

  @Test
  @DisplayName("출석 내역이 없어도 운영 기수의 회원 정보와 빈 출석 목록을 반환한다")
  void returnsActivityWithEmptyAttendances() {
    Long userId = 10L;
    Member activity = new Member(userId, "홍길동", 39, Part.SERVER, 2.0f, 0, 0, 0, 0);
    when(activityPort.findMemberByUserIdAndGeneration(userId, 39))
        .thenReturn(Optional.of(activity));
    when(attendancePort.findAllDetailsByUserIdAndGeneration(userId, activity.generation()))
        .thenReturn(List.of());

    UserAttendanceSummary result = service.getAttendancesByUserId(userId);

    assertThat(result.member()).isEqualTo(activity);
    assertThat(result.lectures()).isEmpty();
  }

  @Test
  @DisplayName("운영 기수의 회원 정보가 없으면 출석 정보를 조회하지 않고 예외를 발생시킨다")
  void rejectsUserWithoutActivity() {
    when(activityPort.findMemberByUserIdAndGeneration(10L, 39)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.getAttendancesByUserId(10L))
        .isInstanceOf(AttendanceException.class)
        .hasMessage(AttendanceFailure.NOT_FOUND_ATTENDANCE.getMessage());
    verifyNoInteractions(attendancePort);
    verify(activityPort, never()).findCurrentActivity(10L);
  }

  @Test
  @DisplayName("운영 기수에서 종료한 세션만 합산하여 점수를 갱신한다")
  void updatesMemberScore() {
    when(activityPort.findMemberByUserIdAndGeneration(10L, 39))
        .thenReturn(Optional.of(new Member(10L, "홍길동", 39, Part.SERVER, 1.5f, 0, 0, 0, 0)));
    when(attendancePort.findAllEndedByUserId(10L, 39)).thenReturn(List.of());
    assertThat(service.updateAttendanceScore(10L)).isEqualTo(2.0f);
    verify(activityPort).updateAttendanceScore(10L, 39, 2.0f);
  }

  @Test
  @DisplayName("운영 기수에 참여하지 않은 회원은 점수를 갱신하지 않는다")
  void rejectsScoreUpdateWithoutMember() {
    when(activityPort.findMemberByUserIdAndGeneration(10L, 39)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> service.updateAttendanceScore(10L))
        .isInstanceOf(AttendanceException.class);
    verifyNoInteractions(attendancePort);
  }

  private final LocalDateTime start = LocalDateTime.now().minusMinutes(1);

  private void validSession() {
    when(subLecturePort.findById(2L))
        .thenReturn(
            Optional.of(new SubLecture(2L, 1L, LectureAttribute.SEMINAR, 39, 1, start, "1234")));
  }

  private void member() {
    when(activityPort.findActivityByUserIdAndGeneration(10L, 39))
        .thenReturn(Optional.of(new UserActivity(10L, "홍길동", 39, Part.SERVER, 2f)));
  }

  private Attendance attendance(boolean ended) {
    return new Attendance(
        3L,
        10L,
        1L,
        "세미나",
        start,
        LectureAttribute.SEMINAR,
        ended,
        AttendanceStatus.ABSENT,
        List.of(
            new SubAttendance(4L, 3L, 2L, 1, start, AttendanceStatus.ABSENT, null),
            new SubAttendance(5L, 3L, 6L, 2, start, AttendanceStatus.ABSENT, null)));
  }

  @Test
  @DisplayName("운영 기수 회원의 출석을 처리하고 세부 세션 ID를 반환한다")
  void marksAttendance() {
    validSession();
    member();
    when(attendancePort.findByLectureIdAndUserId(1L, 10L))
        .thenReturn(Optional.of(attendance(false)));
    assertThat(service.attend(10L, 2L, "1234")).isEqualTo(2L);
    verify(subAttendancePort)
        .save(
            argThat(
                s ->
                    s.id().equals(4L)
                        && s.status() == AttendanceStatus.ATTENDANCE
                        && s.attendedAt() != null));
    verify(attendancePort).updateStatus(3L, AttendanceStatus.TARDY);
    verify(activityPort, never()).updateAttendanceScore(anyLong(), anyInt(), anyFloat());
  }

  @Test
  @DisplayName("출석 코드가 틀리면 회원 조회와 출석 변경을 수행하지 않는다")
  void rejectsWrongCode() {
    validSession();
    assertThatThrownBy(() -> service.attend(10L, 2L, "wrong")).isInstanceOf(LectureException.class);
    verifyNoInteractions(activityPort, attendancePort, subAttendancePort);
  }

  @Test
  @DisplayName("운영 기수에 참여하지 않은 회원의 출석은 거부한다")
  void rejectsInactiveMember() {
    validSession();
    when(activityPort.findActivityByUserIdAndGeneration(10L, 39)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> service.attend(10L, 2L, "1234"))
        .isInstanceOf(AttendanceException.class);
    verifyNoInteractions(attendancePort, subAttendancePort);
  }

  @Test
  @DisplayName("종료한 세션의 출석이 변경되면 운영 기수 점수도 재계산한다")
  void recalculatesEndedScore() {
    validSession();
    member();
    when(attendancePort.findByLectureIdAndUserId(1L, 10L))
        .thenReturn(Optional.of(attendance(true)));
    var updated =
        new Attendance(
            3L,
            10L,
            1L,
            "세미나",
            start,
            LectureAttribute.SEMINAR,
            true,
            AttendanceStatus.TARDY,
            List.of());
    when(attendancePort.findAllEndedByUserId(10L, 39)).thenReturn(List.of(updated));
    service.attend(10L, 2L, "1234");
    verify(activityPort).updateAttendanceScore(10L, 39, 1.5f);
  }

  @Test
  @DisplayName("출석 시작 시각이 있어도 코드가 없으면 출석 시작 전으로 처리한다")
  void rejectsUninitializedCode() {
    when(subLecturePort.findById(2L))
        .thenReturn(
            Optional.of(new SubLecture(2L, 1L, LectureAttribute.SEMINAR, 39, 1, start, null)));
    assertThatThrownBy(() -> service.attend(10L, 2L, "1234")).isInstanceOf(LectureException.class);
    verifyNoInteractions(activityPort, attendancePort, subAttendancePort);
  }

  @Test
  @DisplayName("출석 시간이 지나면 출석 상태를 변경하지 않는다")
  void rejectsExpiredSession() {
    when(subLecturePort.findById(2L))
        .thenReturn(
            Optional.of(
                new SubLecture(
                    2L, 1L, LectureAttribute.SEMINAR, 39, 1, start.minusMinutes(11), "1234")));
    assertThatThrownBy(() -> service.attend(10L, 2L, "1234")).isInstanceOf(LectureException.class);
    verifyNoInteractions(activityPort, attendancePort, subAttendancePort);
  }
}
