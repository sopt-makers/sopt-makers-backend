package org.sopt.makers.storage.db.user.adapter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.lecture.LectureStatus;
import org.sopt.makers.domain.admin.user.AdminUser;
import org.sopt.makers.domain.admin.user.UserActivity;
import org.sopt.makers.domain.admin.user.port.AdminUserActivityPort;
import org.sopt.makers.storage.db.admin.repository.AttendanceJpaRepository;
import org.sopt.makers.storage.db.user.entity.UserActivityHistoryEntity;
import org.sopt.makers.storage.db.user.repository.UserActivityHistoryJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserActivityAdapter implements AdminUserActivityPort {

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

  @Override
  public List<Long> findUserIdsByGenerationAndPart(int generation, Part part) {
    List<UserActivityHistoryEntity> activities =
        (part == null || part == Part.ALL)
            ? activityJpaRepository.findByGenerationAndIsSopt(generation, true)
            : activityJpaRepository.findByGenerationAndPartAndIsSopt(generation, part, true);
    return activities.stream().map(a -> a.getUser().getId()).toList();
  }

  @Transactional
  @Override
  public void updateAttendanceScore(Long userId, int generation, Float score) {
    activityJpaRepository.updateAttendanceScore(userId, generation, score);
  }

  @Transactional
  @Override
  public void bulkUpdateAttendanceScores(int generation, Map<Long, Float> userScores) {
    userScores.forEach(
        (userId, score) -> activityJpaRepository.updateAttendanceScore(userId, generation, score));
  }

  @Override
  public Optional<UserActivity> findCurrentActivity(Long userId) {
    return activityJpaRepository
        .findFirstByUserIdOrderByGenerationDesc(userId)
        .map(
            a ->
                new UserActivity(
                    a.getUser().getId(),
                    a.getUser().getName(),
                    a.getGeneration(),
                    a.getPart(),
                    a.getAttendanceScore()));
  }

  private Map<Long, Map<AttendanceStatus, Integer>> fetchAttendanceCountsByUser(
      List<Long> userIds, int generation) {
    return attendanceJpaRepository
        .countByUserIdsAndGenerationGroupByStatus(userIds, generation, LectureStatus.END)
        .stream()
        .collect(
            Collectors.groupingBy(
                row -> (Long) row[0],
                Collectors.toMap(
                    row -> (AttendanceStatus) row[1], row -> ((Long) row[2]).intValue())));
  }

  private AdminUser toAdminUser(
      UserActivityHistoryEntity activity, Map<Long, Map<AttendanceStatus, Integer>> countsByUser) {
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
