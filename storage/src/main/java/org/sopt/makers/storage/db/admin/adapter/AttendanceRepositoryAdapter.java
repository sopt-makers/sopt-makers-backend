package org.sopt.makers.storage.db.admin.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.admin.attendance.Attendance;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.attendance.SubAttendance;
import org.sopt.makers.domain.admin.attendance.port.AttendanceRepositoryPort;
import org.sopt.makers.storage.db.admin.entity.AttendanceEntity;
import org.sopt.makers.storage.db.admin.entity.SubAttendanceEntity;
import org.sopt.makers.storage.db.admin.repository.AttendanceJpaRepository;
import org.sopt.makers.storage.db.admin.repository.SubAttendanceJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceRepositoryAdapter implements AttendanceRepositoryPort {

  private final AttendanceJpaRepository attendanceJpaRepository;
  private final SubAttendanceJpaRepository subAttendanceJpaRepository;

  @Override
  public Optional<Attendance> findById(Long id) {
    return attendanceJpaRepository.findByIdWithLecture(id).map(this::toAttendanceDomain);
  }

  @Override
  public List<Attendance> findAllByUserId(Long userId) {
    return attendanceJpaRepository.findAllByUserId(userId).stream()
        .map(this::toAttendanceDomain)
        .toList();
  }

  @Override
  public Optional<Attendance> findByLectureIdAndUserId(Long lectureId, Long userId) {
    return attendanceJpaRepository
        .findByLectureIdAndUserId(lectureId, userId)
        .map(this::toAttendanceDomain);
  }

  @Override
  public List<Attendance> findAllEndedByUserId(Long userId, int generation) {
    return attendanceJpaRepository.findAllEndedByUserId(userId, generation).stream()
        .map(this::toAttendanceDomain)
        .toList();
  }

  @Transactional
  @Override
  public void updateStatus(Long attendanceId, AttendanceStatus status) {
    attendanceJpaRepository.updateStatus(attendanceId, status);
  }

  private Attendance toAttendanceDomain(AttendanceEntity entity) {
    List<SubAttendance> subs =
        subAttendanceJpaRepository.findByAttendanceIdWithSubLecture(entity.getId()).stream()
            .map(SubAttendanceEntity::toDomain)
            .toList();
    return entity.toDomain(subs);
  }
}
