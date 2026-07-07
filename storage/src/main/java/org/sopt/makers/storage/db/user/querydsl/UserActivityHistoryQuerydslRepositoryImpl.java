package org.sopt.makers.storage.db.user.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.storage.db.user.entity.QUserActivityHistoryEntity;
import org.sopt.makers.storage.db.user.entity.QUserEntity;
import org.sopt.makers.storage.db.user.entity.UserActivityHistoryEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserActivityHistoryQuerydslRepositoryImpl
    implements UserActivityHistoryQuerydslRepository {

  private static final QUserActivityHistoryEntity activity =
      QUserActivityHistoryEntity.userActivityHistoryEntity;
  private static final QUserEntity user = QUserEntity.userEntity;

  private final JPAQueryFactory queryFactory;
  private final EntityManager em;

  @Override
  public List<UserActivityHistoryEntity> findByGenerationAndPartWithUser(
      int generation, Part part, Pageable pageable) {
    return queryFactory
        .selectFrom(activity)
        .join(activity.user, user)
        .fetchJoin()
        .where(generationPartFilter(generation, part))
        .orderBy(activity.id.asc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();
  }

  @Override
  public int countByGenerationAndPart(int generation, Part part) {
    Long count =
        queryFactory
            .select(activity.count())
            .from(activity)
            .where(generationPartFilter(generation, part))
            .fetchOne();
    return count != null ? count.intValue() : 0;
  }

  @Override
  public List<Long> findUserIdsByGenerationAndPart(int generation, Part part) {
    return queryFactory
        .select(activity.user.id)
        .from(activity)
        .where(generationPartFilter(generation, part))
        .fetch();
  }

  @Override
  public void bulkUpdateAttendanceScores(int generation, Map<Long, Float> userScores) {
    if (userScores.isEmpty()) {
      return;
    }
    queryFactory
        .update(activity)
        .set(activity.attendanceScore, buildScoreCaseExpression(userScores))
        .where(
            activity
                .generation
                .eq(generation)
                .and(activity.user.id.in(userScores.keySet()))
                .and(activity.isSopt.isTrue()))
        .execute();
    em.flush();
    em.clear();
  }

  private NumberExpression<Float> buildScoreCaseExpression(Map<Long, Float> userScores) {
    Iterator<Map.Entry<Long, Float>> iter = userScores.entrySet().iterator();
    Map.Entry<Long, Float> first = iter.next();
    CaseBuilder.Cases<Float, NumberExpression<Float>> cases =
        new CaseBuilder().when(activity.user.id.eq(first.getKey())).then(first.getValue());
    while (iter.hasNext()) {
      Map.Entry<Long, Float> entry = iter.next();
      cases = cases.when(activity.user.id.eq(entry.getKey())).then(entry.getValue());
    }
    return cases.otherwise(activity.attendanceScore);
  }

  private BooleanExpression generationPartFilter(int generation, Part part) {
    BooleanExpression base = activity.generation.eq(generation).and(activity.isSopt.isTrue());
    if (part == null || part == Part.ALL) {
      return base;
    }
    return base.and(activity.part.eq(part));
  }
}
