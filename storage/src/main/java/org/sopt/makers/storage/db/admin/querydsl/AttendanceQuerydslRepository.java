package org.sopt.makers.storage.db.admin.querydsl;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.lecture.LectureStatus;
import org.sopt.makers.storage.db.admin.entity.AttendanceEntity;
import org.sopt.makers.storage.db.admin.projection.AttendanceLectureCountRow;
import org.sopt.makers.storage.db.admin.projection.AttendanceUserCountRow;

public interface AttendanceQuerydslRepository {

  List<AttendanceEntity> findTodayByUserIdAndActivity(
      Long userId, int generation, Part part, LocalDateTime startAt, LocalDateTime endAt);

  List<AttendanceLectureCountRow> countByLectureIdsGroupByStatus(List<Long> lectureIds);

  List<AttendanceUserCountRow> countByUserIdsAndGenerationGroupByStatus(
      List<Long> userIds, int generation, LectureStatus lectureStatus);
}
