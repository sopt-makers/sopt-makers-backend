package org.sopt.makers.domain.admin.attendance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.attendance.MemberAttendanceDetail;
import org.sopt.makers.domain.admin.attendance.UserAttendanceSummary;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceException;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceFailure;
import org.sopt.makers.domain.admin.attendance.port.AttendanceRepositoryPort;
import org.sopt.makers.domain.admin.attendance.port.SubAttendanceRepositoryPort;
import org.sopt.makers.domain.admin.lecture.port.SubLectureRepositoryPort;
import org.sopt.makers.domain.admin.member.Member;
import org.sopt.makers.domain.admin.user.port.AdminUserActivityPort;

class AttendanceServiceTest {

  private final AttendanceRepositoryPort attendancePort = mock(AttendanceRepositoryPort.class);
  private final AdminUserActivityPort activityPort = mock(AdminUserActivityPort.class);
  private final AttendanceService service =
      new AttendanceService(
          mock(SubLectureRepositoryPort.class),
          attendancePort,
          mock(SubAttendanceRepositoryPort.class),
          activityPort,
          39);

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
}
