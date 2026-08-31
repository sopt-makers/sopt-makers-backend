package org.sopt.makers.storage.db.crew.querydsl;

import static org.sopt.makers.storage.db.crew.entity.QMapRecommendEntity.mapRecommendEntity;
import static org.sopt.makers.storage.db.crew.entity.QSoptMapEntity.soptMapEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.pagination.PageQuery;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.crew.soptmap.MapTag;
import org.sopt.makers.domain.crew.soptmap.SoptMapSearchResult;
import org.sopt.makers.domain.crew.soptmap.SoptMapSortType;
import org.sopt.makers.storage.db.crew.entity.SoptMapEntity;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SoptMapQuerydslRepository {

  private static final String CAST_TEXT = "cast({0} as text)";

  private final JPAQueryFactory queryFactory;

  public PageResult<SoptMapSearchResult> search(
      Long userId,
      List<MapTag> mapTags,
      SoptMapSortType sortType,
      List<Long> stationIds,
      PageQuery pageQuery) {
    NumberExpression<Long> recommendCount = mapRecommendEntity.id.count();
    NumberExpression<Integer> recommendedFlag = recommendedFlag(userId);
    List<Tuple> rows =
        queryFactory
            .select(soptMapEntity, recommendCount, recommendedFlag)
            .from(soptMapEntity)
            .leftJoin(mapRecommendEntity)
            .on(
                mapRecommendEntity
                    .soptMapId
                    .eq(soptMapEntity.id)
                    .and(mapRecommendEntity.active.isTrue()))
            .where(mapTagFilter(mapTags), stationFilter(stationIds))
            .groupBy(soptMapEntity.id)
            .orderBy(orderBy(sortType, recommendCount))
            .offset((long) (pageQuery.page() - 1) * pageQuery.limit())
            .limit(pageQuery.limit())
            .fetch();
    long total = count(mapTags, stationIds);
    int totalPages = (int) Math.ceil((double) total / pageQuery.limit());
    List<SoptMapSearchResult> content =
        rows.stream().map(row -> toResult(row, recommendCount, recommendedFlag)).toList();
    return new PageResult<>(
        content,
        total,
        totalPages,
        pageQuery.page(),
        pageQuery.limit(),
        pageQuery.page() < totalPages,
        pageQuery.page() > 1);
  }

  public Optional<SoptMapSearchResult> findDetail(Long userId, Long soptMapId) {
    NumberExpression<Long> recommendCount = mapRecommendEntity.id.count();
    NumberExpression<Integer> recommendedFlag = recommendedFlag(userId);
    Tuple row =
        queryFactory
            .select(soptMapEntity, recommendCount, recommendedFlag)
            .from(soptMapEntity)
            .leftJoin(mapRecommendEntity)
            .on(
                mapRecommendEntity
                    .soptMapId
                    .eq(soptMapEntity.id)
                    .and(mapRecommendEntity.active.isTrue()))
            .where(soptMapEntity.id.eq(soptMapId))
            .groupBy(soptMapEntity.id)
            .fetchOne();
    return Optional.ofNullable(row).map(value -> toResult(value, recommendCount, recommendedFlag));
  }

  private SoptMapSearchResult toResult(
      Tuple row, NumberExpression<Long> recommendCount, NumberExpression<Integer> recommendedFlag) {
    SoptMapEntity entity = row.get(soptMapEntity);
    Long count = row.get(recommendCount);
    Integer recommended = row.get(recommendedFlag);
    return new SoptMapSearchResult(
        entity.toDomain(), count == null ? 0 : count, recommended != null && recommended == 1);
  }

  private long count(List<MapTag> mapTags, List<Long> stationIds) {
    Long count =
        queryFactory
            .select(soptMapEntity.count())
            .from(soptMapEntity)
            .where(mapTagFilter(mapTags), stationFilter(stationIds))
            .fetchOne();
    return count == null ? 0 : count;
  }

  private NumberExpression<Integer> recommendedFlag(Long userId) {
    if (userId == null) {
      return Expressions.ZERO;
    }
    return new CaseBuilder()
        .when(mapRecommendEntity.userId.eq(userId).and(mapRecommendEntity.active.isTrue()))
        .then(1)
        .otherwise(0)
        .max();
  }

  private OrderSpecifier<?>[] orderBy(
      SoptMapSortType sortType, NumberExpression<Long> recommendCount) {
    if (sortType == SoptMapSortType.POPULAR) {
      return new OrderSpecifier<?>[] {recommendCount.desc(), soptMapEntity.createdAt.desc()};
    }
    return new OrderSpecifier<?>[] {soptMapEntity.createdAt.desc(), recommendCount.desc()};
  }

  private Predicate mapTagFilter(List<MapTag> mapTags) {
    if (mapTags == null || mapTags.isEmpty()) {
      return null;
    }
    BooleanBuilder builder = new BooleanBuilder();
    mapTags.forEach(
        tag ->
            builder.or(
                Expressions.stringTemplate(CAST_TEXT, soptMapEntity.mapTags)
                    .like("%\"" + tag.name() + "\"%")));
    return builder;
  }

  private Predicate stationFilter(List<Long> stationIds) {
    if (stationIds == null || stationIds.isEmpty()) {
      return null;
    }
    BooleanBuilder builder = new BooleanBuilder();
    stationIds.forEach(
        stationId -> {
          var stationText = Expressions.stringTemplate(CAST_TEXT, soptMapEntity.nearbyStationIds);
          builder.or(stationText.like("%[" + stationId + ",%"));
          builder.or(stationText.like("%[" + stationId + "]%"));
          builder.or(stationText.like("%," + stationId + ",%"));
          builder.or(stationText.like("%," + stationId + "]"));
        });
    return builder;
  }
}
