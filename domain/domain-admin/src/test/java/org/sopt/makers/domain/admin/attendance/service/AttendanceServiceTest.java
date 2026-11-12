package org.sopt.makers.domain.admin.attendance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.attendance.Attendance;
import org.sopt.makers.domain.admin.attendance.AttendanceDetail;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.attendance.UserAttendanceSummary;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceException;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceFailure;
import org.sopt.makers.domain.admin.attendance.port.AttendanceRepositoryPort;
import org.sopt.makers.domain.admin.attendance.port.SubAttendanceRepositoryPort;
import org.sopt.makers.domain.admin.lecture.LectureAttribute;
import org.sopt.makers.domain.admin.lecture.port.SubLectureRepositoryPort;
import org.sopt.makers.domain.admin.user.UserActivity;
import org.sopt.makers.domain.admin.user.port.AdminUserActivityPort;

class AttendanceServiceTest {

  private final AttendanceRepositoryPort attendancePort = mock(AttendanceRepositoryPort.class);
  private final AdminUserActivityPort activityPort = mock(AdminUserActivityPort.class);
  private final AttendanceService service =
      new AttendanceService(
          mock(SubLectureRepositoryPort.class), attendancePort,
          mock(SubAttendanceRepositoryPort.class), activityPort);

  @Test
  @DisplayName("사용자의 현재 활동 정보와 세션별 출석 상세를 함께 반환한다")
  void returnsActivityAndAttendanceDetails() {
    Long userId = 10L;
    UserActivity activity = new UserActivity(userId, "홍길동", 39, Part.SERVER, 1.5f);
    LocalDateTime start = LocalDateTime.of(2026, 10, 1, 14, 0);
    LocalDateTime end = start.plusHours(4);
    Attendance attendance =
        new Attendance(
            1L,
            userId,
            2L,
            "세미나",
            start,
            LectureAttribute.SEMINAR,
            true,
            AttendanceStatus.TARDY,
            List.of());
    AttendanceDetail detail =
        new AttendanceDetail(
            attendance,
            start,
            end,
            List.of(
                new AttendanceDetail.SubAttendanceDetail(
                    1, AttendanceStatus.ATTENDANCE, start.plusMinutes(10))));
    when(activityPort.findCurrentActivity(userId)).thenReturn(Optional.of(activity));
    when(attendancePort.findAllDetailsByUserId(userId)).thenReturn(List.of(detail));

    UserAttendanceSummary result = service.getAttendancesByUserId(userId);

    assertThat(result.activity()).isEqualTo(activity);
    assertThat(result.lectures()).containsExactly(detail);
  }

  @Test
  @DisplayName("출석 내역이 없어도 현재 활동 정보와 빈 출석 목록을 반환한다")
  void returnsActivityWithEmptyAttendances() {
    Long userId = 10L;
    UserActivity activity = new UserActivity(userId, "홍길동", 39, Part.SERVER, 2.0f);
    when(activityPort.findCurrentActivity(userId)).thenReturn(Optional.of(activity));
    when(attendancePort.findAllDetailsByUserId(userId)).thenReturn(List.of());

    UserAttendanceSummary result = service.getAttendancesByUserId(userId);

    assertThat(result.activity()).isEqualTo(activity);
    assertThat(result.lectures()).isEmpty();
  }

  @Test
  @DisplayName("현재 활동 정보가 없으면 출석 정보를 조회하지 않고 예외를 발생시킨다")
  void rejectsUserWithoutActivity() {
    when(activityPort.findCurrentActivity(10L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.getAttendancesByUserId(10L))
        .isInstanceOf(AttendanceException.class)
        .hasMessage(AttendanceFailure.NOT_FOUND_ATTENDANCE.getMessage());
    verifyNoInteractions(attendancePort);
  }
}
