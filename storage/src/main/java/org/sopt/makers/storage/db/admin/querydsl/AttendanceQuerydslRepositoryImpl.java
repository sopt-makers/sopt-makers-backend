package org.sopt.makers.storage.db.admin.querydsl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.lecture.LectureStatus;
import org.sopt.makers.storage.db.admin.entity.AttendanceEntity;
import org.sopt.makers.storage.db.admin.entity.QAttendanceEntity;
import org.sopt.makers.storage.db.admin.entity.QLectureEntity;
import org.sopt.makers.storage.db.admin.projection.AttendanceLectureCountRow;
import org.sopt.makers.storage.db.admin.projection.AttendanceUserCountRow;
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

  @Override
  public List<AttendanceLectureCountRow> countByLectureIdsGroupByStatus(List<Long> lectureIds) {
    return queryFactory
        .select(
            Projections.constructor(
                AttendanceLectureCountRow.class,
                attendance.lecture.id,
                attendance.status,
                attendance.count()))
        .from(attendance)
        .where(attendance.lecture.id.in(lectureIds))
        .groupBy(attendance.lecture.id, attendance.status)
        .fetch();
  }

  @Override
  public List<AttendanceUserCountRow> countByUserIdsAndGenerationGroupByStatus(
      List<Long> userIds, int generation, LectureStatus lectureStatus) {
    return queryFactory
        .select(
            Projections.constructor(
                AttendanceUserCountRow.class,
                attendance.userId,
                attendance.status,
                attendance.count()))
        .from(attendance)
        .where(
            attendance.userId.in(userIds),
            attendance.lecture.generation.eq(generation),
            attendance.lecture.status.eq(lectureStatus))
        .groupBy(attendance.userId, attendance.status)
        .fetch();
  }
}
