package org.sopt.makers.storage.db.user.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.user.AdminUser;
import org.sopt.makers.domain.admin.user.port.AdminUserQueryPort;
import org.sopt.makers.storage.db.admin.repository.AttendanceJpaRepository;
import org.sopt.makers.storage.db.user.entity.UserActivityHistoryEntity;
import org.sopt.makers.storage.db.user.repository.UserActivityHistoryJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminUserQueryAdapter implements AdminUserQueryPort {

  private final UserActivityHistoryJpaRepository activityJpaRepository;
  private final AttendanceJpaRepository attendanceJpaRepository;

  @Override
  public List<AdminUser> findByGenerationAndPart(int generation, Part part, int page, int limit) {
    List<UserActivityHistoryEntity> activities =
        activityJpaRepository.findByGenerationAndPartWithUser(
            generation, part, PageRequest.of(page, limit));
    return activities.stream().map(a -> toAdminUser(a, generation)).toList();
  }

  @Override
  public int countByGenerationAndPart(int generation, Part part) {
    return activityJpaRepository.countByGenerationAndPart(generation, part);
  }

  private AdminUser toAdminUser(UserActivityHistoryEntity activity, int generation) {
    Long userId = activity.getUser().getId();
    String name = activity.getUser().getName();
    Part part = activity.getPart();
    float score = activity.getAttendanceScore() != null ? activity.getAttendanceScore() : 2.0f;

    int attendanceCount =
        attendanceJpaRepository.countByUserIdAndGenerationAndStatus(
            userId, generation, AttendanceStatus.ATTENDANCE);
    int absentCount =
        attendanceJpaRepository.countByUserIdAndGenerationAndStatus(
            userId, generation, AttendanceStatus.ABSENT);
    int tardyCount =
        attendanceJpaRepository.countByUserIdAndGenerationAndStatus(
            userId, generation, AttendanceStatus.TARDY);
    int participateCount =
        attendanceJpaRepository.countByUserIdAndGenerationAndStatus(
            userId, generation, AttendanceStatus.PARTICIPATE);

    return new AdminUser(
        userId, name, part, score, attendanceCount, absentCount, tardyCount, participateCount);
  }
}
