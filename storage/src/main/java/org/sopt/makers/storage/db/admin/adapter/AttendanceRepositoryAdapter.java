package org.sopt.makers.storage.db.admin.adapter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.attendance.Attendance;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.attendance.SubAttendance;
import org.sopt.makers.domain.admin.attendance.port.AttendanceRepositoryPort;
import org.sopt.makers.domain.admin.lecture.LectureStatus;
import org.sopt.makers.storage.db.admin.entity.AttendanceEntity;
import org.sopt.makers.storage.db.admin.entity.SubAttendanceEntity;
import org.sopt.makers.storage.db.admin.repository.AttendanceJpaRepository;
import org.sopt.makers.storage.db.admin.repository.SubAttendanceJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    return toBatchAttendanceDomain(attendanceJpaRepository.findAllByUserId(userId));
  }

  @Override
  public Optional<Attendance> findByLectureIdAndUserId(Long lectureId, Long userId) {
    return attendanceJpaRepository
        .findByLectureIdAndUserId(lectureId, userId)
        .map(this::toAttendanceDomain);
  }

  @Override
  public List<Attendance> findAllEndedByUserId(Long userId, int generation) {
    return toBatchAttendanceDomain(
        attendanceJpaRepository.findAllEndedByUserId(userId, generation, LectureStatus.END));
  }

  @Override
  public List<Attendance> findAllEndedByUserIds(List<Long> userIds, int generation) {
    return toBatchAttendanceDomain(
        attendanceJpaRepository.findAllEndedByUserIds(userIds, generation, LectureStatus.END));
  }

  @Transactional
  @Override
  public void updateStatus(Long attendanceId, AttendanceStatus status) {
    attendanceJpaRepository.updateStatus(attendanceId, status);
  }

  @Override
  public List<Attendance> findAllByLectureIdAndPart(
      Long lectureId, Part part, int page, int limit) {
    return toBatchAttendanceDomain(
        attendanceJpaRepository.findAllByLectureIdAndPart(
            lectureId, part, PageRequest.of(page, limit, Sort.by("id"))));
  }

  @Override
  public int countByLectureIdAndPart(Long lectureId, Part part) {
    return attendanceJpaRepository.countByLectureIdAndPart(lectureId, part);
  }

  private List<Attendance> toBatchAttendanceDomain(List<AttendanceEntity> entities) {
    if (entities.isEmpty()) {
      return List.of();
    }
    List<Long> attendanceIds = entities.stream().map(AttendanceEntity::getId).toList();
    Map<Long, List<SubAttendanceEntity>> subsByAttendanceId =
        subAttendanceJpaRepository.findAllByAttendanceIdIn(attendanceIds).stream()
            .collect(Collectors.groupingBy(sa -> sa.getAttendance().getId()));
    return entities.stream()
        .map(
            entity -> {
              List<SubAttendance> subs =
                  subsByAttendanceId.getOrDefault(entity.getId(), List.of()).stream()
                      .map(SubAttendanceEntity::toDomain)
                      .toList();
              return entity.toDomain(subs);
            })
        .toList();
  }

  private Attendance toAttendanceDomain(AttendanceEntity entity) {
    List<SubAttendance> subs =
        subAttendanceJpaRepository.findByAttendanceIdWithSubLecture(entity.getId()).stream()
            .map(SubAttendanceEntity::toDomain)
            .toList();
    return entity.toDomain(subs);
  }
}
