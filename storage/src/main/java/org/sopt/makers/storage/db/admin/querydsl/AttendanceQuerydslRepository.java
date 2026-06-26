package org.sopt.makers.storage.db.admin.querydsl;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.storage.db.admin.entity.AttendanceEntity;

public interface AttendanceQuerydslRepository {

  List<AttendanceEntity> findTodayByUserIdAndActivity(
      Long userId, int generation, Part part, LocalDateTime startAt, LocalDateTime endAt);
}
