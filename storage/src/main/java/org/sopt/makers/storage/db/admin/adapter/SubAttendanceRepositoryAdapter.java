package org.sopt.makers.storage.db.admin.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.admin.attendance.SubAttendance;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceException;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceFailure;
import org.sopt.makers.domain.admin.attendance.port.SubAttendanceRepositoryPort;
import org.sopt.makers.storage.db.admin.entity.SubAttendanceEntity;
import org.sopt.makers.storage.db.admin.repository.SubAttendanceJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubAttendanceRepositoryAdapter implements SubAttendanceRepositoryPort {

  private final SubAttendanceJpaRepository subAttendanceJpaRepository;

  @Override
  public Optional<SubAttendance> findById(Long id) {
    return subAttendanceJpaRepository.findById(id).map(SubAttendanceEntity::toDomain);
  }

  @Transactional
  @Override
  public SubAttendance save(SubAttendance subAttendance) {
    SubAttendanceEntity entity =
        subAttendanceJpaRepository
            .findById(subAttendance.id())
            .orElseThrow(() -> new AttendanceException(AttendanceFailure.NOT_FOUND_SUB_ATTENDANCE));
    entity.updateStatus(subAttendance.status());
    return subAttendanceJpaRepository.save(entity).toDomain();
  }
}
