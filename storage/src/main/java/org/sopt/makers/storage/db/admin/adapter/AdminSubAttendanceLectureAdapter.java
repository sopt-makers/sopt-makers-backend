package org.sopt.makers.storage.db.admin.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.admin.attendance.port.AdminSubAttendanceLecturePort;
import org.sopt.makers.storage.db.admin.entity.AttendanceEntity;
import org.sopt.makers.storage.db.admin.entity.SubAttendanceEntity;
import org.sopt.makers.storage.db.admin.entity.SubLectureEntity;
import org.sopt.makers.storage.db.admin.repository.AttendanceJpaRepository;
import org.sopt.makers.storage.db.admin.repository.SubAttendanceJpaRepository;
import org.sopt.makers.storage.db.admin.repository.SubLectureJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminSubAttendanceLectureAdapter implements AdminSubAttendanceLecturePort {

  private final SubAttendanceJpaRepository subAttendanceJpaRepository;
  private final AttendanceJpaRepository attendanceJpaRepository;
  private final SubLectureJpaRepository subLectureJpaRepository;

  @Override
  @Transactional
  public void saveAllForAttendances(List<Long> attendanceIds, List<Long> subLectureIds) {
    List<AttendanceEntity> attendances = attendanceJpaRepository.findAllById(attendanceIds);
    List<SubLectureEntity> subLectures = subLectureJpaRepository.findAllById(subLectureIds);
    List<SubAttendanceEntity> entities =
        attendances.stream()
            .flatMap(a -> subLectures.stream().map(sl -> SubAttendanceEntity.create(a, sl)))
            .toList();
    subAttendanceJpaRepository.saveAll(entities);
  }

  @Override
  @Transactional
  public void deleteAllBySubLectureIds(List<Long> subLectureIds) {
    if (!subLectureIds.isEmpty()) {
      subAttendanceJpaRepository.deleteAllBySubLectureIdIn(subLectureIds);
    }
  }
}
