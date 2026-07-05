package org.sopt.makers.storage.db.user.adapter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

    List<Long> userIds = activities.stream().map(a -> a.getUser().getId()).toList();
    Map<Long, Map<AttendanceStatus, Integer>> countsByUser =
        fetchAttendanceCountsByUser(userIds, generation);

    return activities.stream().map(a -> toAdminUser(a, countsByUser)).toList();
  }

  @Override
  public int countByGenerationAndPart(int generation, Part part) {
    return activityJpaRepository.countByGenerationAndPart(generation, part);
  }

  private Map<Long, Map<AttendanceStatus, Integer>> fetchAttendanceCountsByUser(
      List<Long> userIds, int generation) {
    return attendanceJpaRepository
        .countByUserIdsAndGenerationGroupByStatus(userIds, generation)
        .stream()
        .collect(
            Collectors.groupingBy(
                row -> (Long) row[0],
                Collectors.toMap(
                    row -> (AttendanceStatus) row[1], row -> ((Long) row[2]).intValue())));
  }

  private AdminUser toAdminUser(
      UserActivityHistoryEntity activity,
      Map<Long, Map<AttendanceStatus, Integer>> countsByUser) {
    Long userId = activity.getUser().getId();
    Map<AttendanceStatus, Integer> counts = countsByUser.getOrDefault(userId, Map.of());
    return new AdminUser(
        userId,
        activity.getUser().getName(),
        activity.getPart(),
        activity.getAttendanceScore() != null ? activity.getAttendanceScore() : 2.0f,
        counts.getOrDefault(AttendanceStatus.ATTENDANCE, 0),
        counts.getOrDefault(AttendanceStatus.ABSENT, 0),
        counts.getOrDefault(AttendanceStatus.TARDY, 0),
        counts.getOrDefault(AttendanceStatus.PARTICIPATE, 0));
  }
}
