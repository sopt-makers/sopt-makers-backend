package org.sopt.makers.domain.admin.app.service;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.admin.app.AppMemberAttendanceSummary;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceException;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceFailure;
import org.sopt.makers.domain.admin.attendance.port.AttendanceRepositoryPort;
import org.sopt.makers.domain.admin.user.UserActivity;
import org.sopt.makers.domain.admin.user.port.AdminUserActivityPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppMemberService {

  private final AdminUserActivityPort adminUserActivityPort;
  private final AttendanceRepositoryPort attendanceRepositoryPort;

  public AppMemberAttendanceSummary getMemberTotalAttendance(Long userId) {
    UserActivity activity = findCurrentActivity(userId);
    return new AppMemberAttendanceSummary(
        activity,
        attendanceRepositoryPort.findAllEndedByUserId(activity.userId(), activity.generation()));
  }

  public float getMemberScore(Long userId) {
    UserActivity activity = findCurrentActivity(userId);
    return activity.attendanceScore() == null ? 0f : activity.attendanceScore();
  }

  private UserActivity findCurrentActivity(Long userId) {
    return adminUserActivityPort
        .findCurrentActivity(userId)
        .orElseThrow(() -> new AttendanceException(AttendanceFailure.NOT_FOUND_ATTENDANCE));
  }
}
