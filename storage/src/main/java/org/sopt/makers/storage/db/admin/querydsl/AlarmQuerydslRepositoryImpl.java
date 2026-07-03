package org.sopt.makers.storage.db.admin.querydsl;

import static java.util.Objects.nonNull;
import static org.sopt.makers.storage.db.admin.entity.QAlarmEntity.alarmEntity;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.admin.alarm.AlarmStatus;
import org.sopt.makers.storage.db.admin.entity.AlarmEntity;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AlarmQuerydslRepositoryImpl implements AlarmQuerydslRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<AlarmEntity> findOrderByCreatedAt(
      Integer generation, AlarmStatus status, int page, int size) {
    return queryFactory
        .selectFrom(alarmEntity)
        .where(generationEq(generation), statusEq(status))
        .orderBy(alarmEntity.createdAt.desc())
        .offset((long) page * size)
        .limit(size)
        .fetch();
  }

  @Override
  public int count(Integer generation, AlarmStatus status) {
    Long result =
        queryFactory
            .select(alarmEntity.count())
            .from(alarmEntity)
            .where(generationEq(generation), statusEq(status))
            .fetchFirst();
    return result == null ? 0 : Math.toIntExact(result);
  }

  private BooleanExpression generationEq(Integer generation) {
    return nonNull(generation) ? alarmEntity.generation.eq(generation) : null;
  }

  private BooleanExpression statusEq(AlarmStatus status) {
    return nonNull(status) ? alarmEntity.status.eq(status) : null;
  }
}
