package org.sopt.makers.domain.admin.app.service;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.admin.app.AppMemberAttendanceSummary;
import org.sopt.makers.domain.admin.app.AppUserActivity;
import org.sopt.makers.domain.admin.app.port.AppUserActivityPort;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceException;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceFailure;
import org.sopt.makers.domain.admin.attendance.port.AttendanceRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppMemberService {

  private final AppUserActivityPort appUserActivityPort;
  private final AttendanceRepositoryPort attendanceRepositoryPort;

  public AppMemberAttendanceSummary getMemberTotalAttendance(Long userId) {
    AppUserActivity activity = findCurrentActivity(userId);
    return new AppMemberAttendanceSummary(
        activity,
        attendanceRepositoryPort.findAllEndedByUserId(activity.userId(), activity.generation()));
  }

  public float getMemberScore(Long userId) {
    AppUserActivity activity = findCurrentActivity(userId);
    return activity.attendanceScore() == null ? 0f : activity.attendanceScore();
  }

  private AppUserActivity findCurrentActivity(Long userId) {
    return appUserActivityPort
        .findCurrentActivity(userId)
        .orElseThrow(() -> new AttendanceException(AttendanceFailure.NOT_FOUND_ATTENDANCE));
  }
}
