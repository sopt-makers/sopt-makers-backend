package org.sopt.makers.storage.db.admin.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.storage.db.admin.entity.AttendanceEntity;
import org.sopt.makers.storage.db.admin.entity.QAttendanceEntity;
import org.sopt.makers.storage.db.admin.entity.QLectureEntity;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AttendanceQuerydslRepositoryImpl implements AttendanceQuerydslRepository {

  private static final QAttendanceEntity attendance = QAttendanceEntity.attendanceEntity;
  private static final QLectureEntity lecture = QLectureEntity.lectureEntity;

  private final JPAQueryFactory queryFactory;

  @Override
  public List<AttendanceEntity> findTodayByUserIdAndActivity(
      Long userId, int generation, Part part, LocalDateTime startAt, LocalDateTime endAt) {
    return queryFactory
        .selectFrom(attendance)
        .join(attendance.lecture, lecture)
        .fetchJoin()
        .where(
            attendance.userId.eq(userId),
            attendance.lecture.generation.eq(generation),
            attendance.lecture.part.eq(part).or(attendance.lecture.part.eq(Part.ALL)),
            attendance.lecture.startDate.between(startAt, endAt))
        .orderBy(attendance.lecture.startDate.asc())
        .fetch();
  }
}
