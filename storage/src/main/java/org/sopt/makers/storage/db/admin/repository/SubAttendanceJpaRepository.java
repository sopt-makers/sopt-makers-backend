package org.sopt.makers.storage.db.admin.repository;

import java.util.List;
import org.sopt.makers.storage.db.admin.entity.SubAttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubAttendanceJpaRepository extends JpaRepository<SubAttendanceEntity, Long> {

  @Query(
      "SELECT sa FROM SubAttendanceEntity sa JOIN FETCH sa.subLecture"
          + " WHERE sa.attendance.id = :attendanceId")
  List<SubAttendanceEntity> findByAttendanceIdWithSubLecture(
      @Param("attendanceId") Long attendanceId);
}
