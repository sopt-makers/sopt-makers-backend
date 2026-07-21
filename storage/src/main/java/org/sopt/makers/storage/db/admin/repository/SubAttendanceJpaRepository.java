package org.sopt.makers.storage.db.admin.repository;

import java.util.List;
import org.sopt.makers.storage.db.admin.entity.SubAttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubAttendanceJpaRepository extends JpaRepository<SubAttendanceEntity, Long> {

  @Query(
      "SELECT sa FROM SubAttendanceEntity sa JOIN FETCH sa.subLecture"
          + " WHERE sa.attendance.id = :attendanceId")
  List<SubAttendanceEntity> findByAttendanceIdWithSubLecture(
      @Param("attendanceId") Long attendanceId);

  @Query(
      "SELECT sa FROM SubAttendanceEntity sa JOIN FETCH sa.subLecture JOIN FETCH sa.attendance"
          + " WHERE sa.attendance.id IN :attendanceIds")
  List<SubAttendanceEntity> findAllByAttendanceIdIn(
      @Param("attendanceIds") List<Long> attendanceIds);

  @Modifying(clearAutomatically = true)
  @Query("DELETE FROM SubAttendanceEntity sa WHERE sa.subLecture.id IN :subLectureIds")
  void deleteAllBySubLectureIdIn(@Param("subLectureIds") List<Long> subLectureIds);
}
