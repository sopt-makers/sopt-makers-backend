package org.sopt.makers.storage.db.admin.adapter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.attendance.port.AttendanceLecturePort;
import org.sopt.makers.storage.db.admin.entity.AttendanceEntity;
import org.sopt.makers.storage.db.admin.entity.LectureEntity;
import org.sopt.makers.storage.db.admin.projection.AttendanceLectureCountRow;
import org.sopt.makers.storage.db.admin.repository.AttendanceJpaRepository;
import org.sopt.makers.storage.db.admin.repository.LectureJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceLectureAdapter implements AttendanceLecturePort {

  private final AttendanceJpaRepository attendanceJpaRepository;
  private final LectureJpaRepository lectureJpaRepository;

  @Override
  @Transactional
  public List<Long> saveAllForUsers(Long lectureId, List<Long> userIds) {
    LectureEntity lecture = lectureJpaRepository.getReferenceById(lectureId);
    List<AttendanceEntity> entities =
        userIds.stream().map(userId -> AttendanceEntity.create(userId, lecture)).toList();
    return attendanceJpaRepository.saveAll(entities).stream().map(AttendanceEntity::getId).toList();
  }

  @Override
  public List<Long> getUserIdsByLectureId(Long lectureId) {
    return attendanceJpaRepository.findUserIdsByLectureId(lectureId);
  }

  @Override
  public List<Long> getAttendanceIdsByLectureId(Long lectureId) {
    return attendanceJpaRepository.findIdsByLectureId(lectureId);
  }

  @Override
  public Map<Long, Map<AttendanceStatus, Integer>> countByLectureIdsGroupByStatus(
      List<Long> lectureIds) {
    return attendanceJpaRepository.countByLectureIdsGroupByStatus(lectureIds).stream()
        .collect(
            Collectors.groupingBy(
                AttendanceLectureCountRow::lectureId,
                Collectors.toMap(
                    AttendanceLectureCountRow::status, row -> row.count().intValue())));
  }

  @Override
  @Transactional
  public void deleteByLectureId(Long lectureId) {
    attendanceJpaRepository.deleteAllByLectureId(lectureId);
  }
}
