package org.sopt.makers.storage.db.user.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.admin.attendance.port.AttendanceUserActivityPort;
import org.sopt.makers.storage.db.user.repository.UserActivityHistoryJpaRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AttendanceUserActivityAdapter implements AttendanceUserActivityPort {

  private final UserActivityHistoryJpaRepository activityJpaRepository;

  @Override
  public void updateAttendanceScore(Long userId, int generation, Float score) {
    activityJpaRepository.updateAttendanceScore(userId, generation, score);
  }
}
