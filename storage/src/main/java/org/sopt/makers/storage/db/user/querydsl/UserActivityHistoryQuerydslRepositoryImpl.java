package org.sopt.makers.storage.db.user.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
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

  @Override
  public List<UserActivityHistoryEntity> findByGenerationAndPartWithUser(
      int generation, Part part, Pageable pageable) {
    return queryFactory
        .selectFrom(activity)
        .join(activity.user, user)
        .fetchJoin()
        .where(generationPartFilter(generation, part))
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

  private BooleanExpression generationPartFilter(int generation, Part part) {
    BooleanExpression base = activity.generation.eq(generation).and(activity.isSopt.isTrue());
    if (part == null || part == Part.ALL) {
      return base;
    }
    return base.and(activity.part.eq(part));
  }
}
