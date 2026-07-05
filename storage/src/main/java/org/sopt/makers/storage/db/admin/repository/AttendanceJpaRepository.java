package org.sopt.makers.storage.db.admin.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.lecture.LectureStatus;
import org.sopt.makers.storage.db.admin.entity.AttendanceEntity;
import org.sopt.makers.storage.db.admin.querydsl.AttendanceQuerydslRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttendanceJpaRepository
    extends JpaRepository<AttendanceEntity, Long>, AttendanceQuerydslRepository {

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
          + " AND a.lecture.status = :lectureStatus")
  List<AttendanceEntity> findAllEndedByUserId(
      @Param("userId") Long userId,
      @Param("generation") int generation,
      @Param("lectureStatus") LectureStatus lectureStatus);

  @Query(
      "SELECT a FROM AttendanceEntity a JOIN FETCH a.lecture"
          + " WHERE a.userId IN :userIds AND a.lecture.generation = :generation"
          + " AND a.lecture.status = :lectureStatus")
  List<AttendanceEntity> findAllEndedByUserIds(
      @Param("userIds") List<Long> userIds,
      @Param("generation") int generation,
      @Param("lectureStatus") LectureStatus lectureStatus);

  @Modifying(clearAutomatically = true)
  @Query("UPDATE AttendanceEntity a SET a.status = :status WHERE a.id = :id")
  void updateStatus(@Param("id") Long id, @Param("status") AttendanceStatus status);

  @Query("SELECT a FROM AttendanceEntity a JOIN FETCH a.lecture WHERE a.lecture.id = :lectureId")
  List<AttendanceEntity> findAllByLectureId(@Param("lectureId") Long lectureId);

  @Query("SELECT a.userId FROM AttendanceEntity a WHERE a.lecture.id = :lectureId")
  List<Long> findUserIdsByLectureId(@Param("lectureId") Long lectureId);

  @Query("SELECT a.id FROM AttendanceEntity a WHERE a.lecture.id = :lectureId")
  List<Long> findIdsByLectureId(@Param("lectureId") Long lectureId);

  @Query(
      "SELECT COUNT(a) FROM AttendanceEntity a WHERE a.lecture.id = :lectureId"
          + " AND a.status = :status")
  int countByLectureIdAndStatus(
      @Param("lectureId") Long lectureId, @Param("status") AttendanceStatus status);

  @Query(
      "SELECT a FROM AttendanceEntity a JOIN FETCH a.lecture WHERE a.lecture.id = :lectureId"
          + " AND (:part IS NULL OR a.lecture.part = :part"
          + " OR a.lecture.part = org.sopt.makers.core.type.Part.ALL)")
  List<AttendanceEntity> findAllByLectureIdAndPart(
      @Param("lectureId") Long lectureId, @Param("part") Part part, Pageable pageable);

  @Query(
      "SELECT COUNT(a) FROM AttendanceEntity a WHERE a.lecture.id = :lectureId"
          + " AND (:part IS NULL OR a.lecture.part = :part"
          + " OR a.lecture.part = org.sopt.makers.core.type.Part.ALL)")
  int countByLectureIdAndPart(@Param("lectureId") Long lectureId, @Param("part") Part part);

  void deleteAllByLectureId(Long lectureId);

  @Query(
      "SELECT COUNT(a) FROM AttendanceEntity a"
          + " WHERE a.userId = :userId AND a.lecture.generation = :generation"
          + " AND a.status = :status AND a.lecture.status = :lectureStatus")
  int countByUserIdAndGenerationAndStatus(
      @Param("userId") Long userId,
      @Param("generation") int generation,
      @Param("status") AttendanceStatus status,
      @Param("lectureStatus") LectureStatus lectureStatus);

  @Query(
      "SELECT a.userId, a.status, COUNT(a) FROM AttendanceEntity a"
          + " WHERE a.userId IN :userIds AND a.lecture.generation = :generation"
          + " AND a.lecture.status = :lectureStatus"
          + " GROUP BY a.userId, a.status")
  List<Object[]> countByUserIdsAndGenerationGroupByStatus(
      @Param("userIds") List<Long> userIds,
      @Param("generation") int generation,
      @Param("lectureStatus") LectureStatus lectureStatus);
}
