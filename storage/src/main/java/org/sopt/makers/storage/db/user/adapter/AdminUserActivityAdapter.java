package org.sopt.makers.storage.db.user.adapter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.attendance.Attendance;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.member.Member;
import org.sopt.makers.domain.admin.user.UserActivity;
import org.sopt.makers.domain.admin.user.port.AdminUserActivityPort;
import org.sopt.makers.storage.db.admin.projection.AttendanceUserCountRow;
import org.sopt.makers.storage.db.admin.repository.AttendanceJpaRepository;
import org.sopt.makers.storage.db.user.entity.UserActivityHistoryEntity;
import org.sopt.makers.storage.db.user.querydsl.UserActivityHistoryQuerydslRepository;
import org.sopt.makers.storage.db.user.repository.UserActivityHistoryJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserActivityAdapter implements AdminUserActivityPort {

  private final UserActivityHistoryJpaRepository activityJpaRepository;
  private final UserActivityHistoryQuerydslRepository activityQuerydslRepository;
  private final AttendanceJpaRepository attendanceJpaRepository;

  @Override
  public List<Member> findByGenerationAndPart(int generation, Part part, int page, int limit) {
    List<UserActivityHistoryEntity> activities =
        activityQuerydslRepository.findByGenerationAndPartWithUser(
            generation, part, PageRequest.of(page, limit));

    List<Long> userIds = activities.stream().map(a -> a.getUser().getId()).toList();
    Map<Long, Map<AttendanceStatus, Integer>> countsByUser =
        fetchAttendanceCountsByUser(userIds, generation);

    return activities.stream().map(a -> toMember(a, countsByUser)).toList();
  }

  @Override
  public int countByGenerationAndPart(int generation, Part part) {
    return activityQuerydslRepository.countByGenerationAndPart(generation, part);
  }

  @Override
  public List<Long> findUserIdsByGenerationAndPart(int generation, Part part) {
    return activityQuerydslRepository.findUserIdsByGenerationAndPart(generation, part);
  }

  @Transactional
  @Override
  public void updateAttendanceScore(Long userId, int generation, Float score) {
    activityJpaRepository.updateAttendanceScore(userId, generation, score);
  }

  @Transactional
  @Override
  public void bulkUpdateAttendanceScores(int generation, Map<Long, Float> userScores) {
    activityQuerydslRepository.bulkUpdateAttendanceScores(generation, userScores);
  }

  @Override
  public Optional<Member> findMemberByUserIdAndGeneration(Long userId, int generation) {
    return activityJpaRepository
        .findSoptActivityByUserIdAndGeneration(userId, generation)
        .map(a -> toMember(a, fetchAttendanceCountsByUser(List.of(userId), a.getGeneration())));
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

  @Override
  public Optional<UserActivity> findActivityByUserIdAndGeneration(Long userId, int generation) {
    return activityJpaRepository
        .findSoptActivityByUserIdAndGeneration(userId, generation)
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
        .countByUserIdsAndGenerationGroupByStatus(userIds, generation)
        .stream()
        .collect(
            Collectors.groupingBy(
                AttendanceUserCountRow::userId,
                Collectors.toMap(AttendanceUserCountRow::status, row -> row.count().intValue())));
  }

  private Member toMember(
      UserActivityHistoryEntity activity, Map<Long, Map<AttendanceStatus, Integer>> countsByUser) {
    Long userId = activity.getUser().getId();
    Map<AttendanceStatus, Integer> counts = countsByUser.getOrDefault(userId, Map.of());
    return new Member(
        userId,
        activity.getUser().getName(),
        activity.getGeneration(),
        activity.getPart(),
        activity.getAttendanceScore() != null
            ? activity.getAttendanceScore()
            : Attendance.BASE_SCORE,
        counts.getOrDefault(AttendanceStatus.ATTENDANCE, 0),
        counts.getOrDefault(AttendanceStatus.ABSENT, 0),
        counts.getOrDefault(AttendanceStatus.TARDY, 0),
        counts.getOrDefault(AttendanceStatus.PARTICIPATE, 0));
  }
}
