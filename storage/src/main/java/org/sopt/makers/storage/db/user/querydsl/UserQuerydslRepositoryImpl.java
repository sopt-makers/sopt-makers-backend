package org.sopt.makers.storage.db.user.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.Role;
import org.sopt.makers.domain.user.UserSearchCondition;
import org.sopt.makers.domain.user.UserSortType;
import org.sopt.makers.storage.db.user.entity.QUserActivityHistoryEntity;
import org.sopt.makers.storage.db.user.entity.QUserCareerEntity;
import org.sopt.makers.storage.db.user.entity.QUserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class UserQuerydslRepositoryImpl implements UserQuerydslRepository {

  private static final QUserEntity user = QUserEntity.userEntity;
  private static final QUserActivityHistoryEntity activity =
      QUserActivityHistoryEntity.userActivityHistoryEntity;
  private static final QUserCareerEntity career = QUserCareerEntity.userCareerEntity;

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<Long> findUserIdsByCondition(
      UserSearchCondition condition, Pageable pageable, UserSortType sortType) {
    BooleanBuilder predicate = buildPredicate(condition);
    OrderSpecifier<?>[] orderBy = buildOrderBy(sortType);

    List<Long> ids =
        queryFactory
            .select(user.id)
            .from(user)
            .join(activity)
            .on(activity.user.id.eq(user.id))
            .where(predicate)
            .groupBy(user.id)
            .orderBy(orderBy)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    Long total =
        queryFactory
            .select(user.id.countDistinct())
            .from(user)
            .join(activity)
            .on(activity.user.id.eq(user.id))
            .where(predicate)
            .fetchOne();

    return new PageImpl<>(ids, pageable, total != null ? total : 0L);
  }

  @Override
  public Set<Long> findUserIdsByRecommendCondition(
      Set<Integer> generations, String mbti, String university) {
    BooleanBuilder predicate = new BooleanBuilder();

    if (generations != null && !generations.isEmpty()) {
      predicate.and(activity.generation.in(generations));
    }
    if (mbti != null) {
      predicate.and(user.mbti.eq(mbti));
    }
    if (university != null) {
      predicate.and(user.university.eq(university));
    }

    return Set.copyOf(
        queryFactory
            .select(user.id)
            .distinct()
            .from(user)
            .join(activity)
            .on(activity.user.id.eq(user.id))
            .where(predicate)
            .fetch());
  }

  private BooleanBuilder buildPredicate(UserSearchCondition condition) {
    BooleanBuilder builder = new BooleanBuilder();

    if (condition.generation() != null) {
      builder.and(activity.generation.eq(condition.generation()));
    }
    if (condition.part() != null) {
      builder.and(activity.part.eq(condition.part()));
    }
    if (condition.name() != null && !condition.name().isBlank()) {
      builder.and(user.name.contains(condition.name()));
    }
    if (condition.team() != null) {
      builder.and(activity.team.eq(condition.team()));
    }
    if (Boolean.TRUE.equals(condition.isAdmin())) {
      builder.and(activity.role.ne(Role.MEMBER));
    }

    return builder;
  }

  @Override
  public List<Long> findUserIdsWithProfileByMbtiAndEmployed(String mbti, Boolean employed) {
    BooleanBuilder predicate = new BooleanBuilder();
    predicate.and(user.isFirstLogin.isFalse());
    if (StringUtils.hasText(mbti)) {
      predicate.and(user.mbti.eq(mbti));
    }
    if (employed != null) {
      var hasCurrentCareer =
          JPAExpressions.selectOne()
              .from(career)
              .where(career.userId.eq(user.id).and(career.isCurrent.isTrue()))
              .exists();
      predicate.and(employed ? hasCurrentCareer : hasCurrentCareer.not());
    }

    return queryFactory.select(user.id).from(user).where(predicate).fetch();
  }

  private OrderSpecifier<?>[] buildOrderBy(UserSortType sortType) {
    return switch (sortType) {
      case LATEST_REGISTERED -> new OrderSpecifier<?>[] {user.id.desc()};
      case OLDEST_REGISTERED -> new OrderSpecifier<?>[] {user.id.asc()};
      case LATEST_GENERATION ->
          new OrderSpecifier<?>[] {activity.generation.max().desc(), user.id.desc()};
      case OLDEST_GENERATION ->
          new OrderSpecifier<?>[] {activity.generation.min().asc(), user.id.asc()};
    };
  }
}
