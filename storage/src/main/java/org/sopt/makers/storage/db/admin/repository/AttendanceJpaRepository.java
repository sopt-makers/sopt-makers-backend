package org.sopt.makers.storage.db.admin.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.storage.db.admin.entity.AttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttendanceJpaRepository extends JpaRepository<AttendanceEntity, Long> {

  @Query("SELECT a FROM AttendanceEntity a JOIN FETCH a.lecture WHERE a.id = :id")
  Optional<AttendanceEntity> findByIdWithLecture(@Param("id") Long id);

  @Query(
      "SELECT a FROM AttendanceEntity a JOIN FETCH a.lecture"
          + " WHERE a.lecture.id = :lectureId AND a.userId = :userId")
  Optional<AttendanceEntity> findByLectureIdAndUserId(
      @Param("lectureId") Long lectureId, @Param("userId") Long userId);

  @Query("SELECT a FROM AttendanceEntity a JOIN FETCH a.lecture WHERE a.userId = :userId")
  List<AttendanceEntity> findAllByUserId(@Param("userId") Long userId);

  @Query(
      "SELECT a FROM AttendanceEntity a JOIN FETCH a.lecture"
          + " WHERE a.userId = :userId AND a.lecture.generation = :generation"
          + " AND a.lecture.status = 'END'")
  List<AttendanceEntity> findAllEndedByUserId(
      @Param("userId") Long userId, @Param("generation") int generation);

  @Modifying
  @Query("UPDATE AttendanceEntity a SET a.status = :status WHERE a.id = :id")
  void updateStatus(@Param("id") Long id, @Param("status") AttendanceStatus status);
}
