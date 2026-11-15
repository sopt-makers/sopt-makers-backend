package org.sopt.makers.domain.admin.app.service;

import org.sopt.makers.domain.admin.app.AppMemberAttendanceSummary;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceException;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceFailure;
import org.sopt.makers.domain.admin.attendance.port.AttendanceRepositoryPort;
import org.sopt.makers.domain.admin.member.Member;
import org.sopt.makers.domain.admin.user.port.AdminUserActivityPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AppMemberService {

  private final AdminUserActivityPort adminUserActivityPort;
  private final AttendanceRepositoryPort attendanceRepositoryPort;
  private final int currentGeneration;

  public AppMemberService(
      AdminUserActivityPort adminUserActivityPort,
      AttendanceRepositoryPort attendanceRepositoryPort,
      @Value("${sopt.current.generation}") int currentGeneration) {
    this.adminUserActivityPort = adminUserActivityPort;
    this.attendanceRepositoryPort = attendanceRepositoryPort;
    this.currentGeneration = currentGeneration;
  }

  public AppMemberAttendanceSummary getMemberTotalAttendance(Long userId) {
    Member activity = findCurrentMember(userId);
    return new AppMemberAttendanceSummary(
        activity,
        attendanceRepositoryPort.findAllEndedByUserId(activity.id(), activity.generation()));
  }

  public float getMemberScore(Long userId) {
    Member activity = findCurrentMember(userId);
    return activity.attendanceScore();
  }

  private Member findCurrentMember(Long userId) {
    return adminUserActivityPort
        .findMemberByUserIdAndGeneration(userId, currentGeneration)
        .orElseThrow(() -> new AttendanceException(AttendanceFailure.NOT_FOUND_ATTENDANCE));
  }
}
