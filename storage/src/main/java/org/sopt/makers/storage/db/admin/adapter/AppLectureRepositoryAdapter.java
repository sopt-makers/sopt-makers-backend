package org.sopt.makers.storage.db.admin.adapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.app.AppLecture;
import org.sopt.makers.domain.admin.app.AppSubLecture;
import org.sopt.makers.domain.admin.app.port.AppLectureRepositoryPort;
import org.sopt.makers.domain.admin.attendance.SubAttendance;
import org.sopt.makers.storage.db.admin.entity.AttendanceEntity;
import org.sopt.makers.storage.db.admin.entity.LectureEntity;
import org.sopt.makers.storage.db.admin.entity.SubAttendanceEntity;
import org.sopt.makers.storage.db.admin.entity.SubLectureEntity;
import org.sopt.makers.storage.db.admin.repository.AttendanceJpaRepository;
import org.sopt.makers.storage.db.admin.repository.LectureJpaRepository;
import org.sopt.makers.storage.db.admin.repository.SubAttendanceJpaRepository;
import org.sopt.makers.storage.db.admin.repository.SubLectureJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppLectureRepositoryAdapter implements AppLectureRepositoryPort {

  private final AttendanceJpaRepository attendanceJpaRepository;
  private final LectureJpaRepository lectureJpaRepository;
  private final SubAttendanceJpaRepository subAttendanceJpaRepository;
  private final SubLectureJpaRepository subLectureJpaRepository;

  @Override
  public List<AppLecture> findTodayLectures(Long userId, int generation, Part part) {
    LocalDate today = LocalDate.now();
    LocalDateTime startAt = today.atStartOfDay();
    LocalDateTime endAt = LocalDateTime.of(today, LocalTime.MAX);
    return attendanceJpaRepository
        .findTodayByUserIdAndActivity(userId, generation, part, startAt, endAt)
        .stream()
        .map(this::toAppLecture)
        .toList();
  }

  @Override
  public Optional<AppLecture> findById(Long lectureId) {
    return lectureJpaRepository
        .findById(lectureId)
        .map(lecture -> toAppLecture(lecture, List.of()));
  }

  @Override
  public Optional<AppSubLecture> findSubLectureByLectureIdAndRound(Long lectureId, int round) {
    return subLectureJpaRepository
        .findByLectureIdAndRound(lectureId, round)
        .map(this::toAppSubLecture);
  }

  private AppLecture toAppLecture(AttendanceEntity attendance) {
    return toAppLecture(
        attendance.getLecture(),
        subAttendanceJpaRepository.findByAttendanceIdWithSubLecture(attendance.getId()).stream()
            .map(SubAttendanceEntity::toDomain)
            .toList());
  }

  private AppLecture toAppLecture(LectureEntity lecture, List<SubAttendance> subAttendances) {
    return new AppLecture(
        lecture.getId(),
        lecture.getPlace(),
        lecture.getName(),
        lecture.getStartDate(),
        lecture.getEndDate(),
        lecture.getAttribute(),
        lecture.getStatus(),
        subAttendances);
  }

  private AppSubLecture toAppSubLecture(SubLectureEntity entity) {
    return new AppSubLecture(entity.getId(), entity.getRound(), entity.getStartAt());
  }
}
