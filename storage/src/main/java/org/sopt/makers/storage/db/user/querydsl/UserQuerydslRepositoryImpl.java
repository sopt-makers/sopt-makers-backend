package org.sopt.makers.storage.db.user.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.Role;
import org.sopt.makers.domain.user.UserSearchCondition;
import org.sopt.makers.domain.user.UserSortType;
import org.sopt.makers.storage.db.user.entity.QUserActivityHistoryEntity;
import org.sopt.makers.storage.db.user.entity.QUserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserQuerydslRepositoryImpl implements UserQuerydslRepository {

  private static final QUserEntity user = QUserEntity.userEntity;
  private static final QUserActivityHistoryEntity activity =
      QUserActivityHistoryEntity.userActivityHistoryEntity;

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
