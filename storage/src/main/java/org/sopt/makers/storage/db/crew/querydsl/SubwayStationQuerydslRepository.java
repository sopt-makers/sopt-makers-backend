package org.sopt.makers.storage.db.crew.querydsl;

import static org.sopt.makers.storage.db.crew.entity.QSubwayStationEntity.subwayStationEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.storage.db.crew.entity.SubwayStationEntity;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SubwayStationQuerydslRepository {

  private static final double SIMILARITY_THRESHOLD = 0.5;
  private static final int LIMIT = 5;

  private final JPAQueryFactory queryFactory;

  public List<SubwayStationEntity> search(String keyword) {
    if (keyword == null || keyword.isBlank()) {
      return queryFactory
          .selectFrom(subwayStationEntity)
          .orderBy(subwayStationEntity.id.desc())
          .limit(LIMIT)
          .fetch();
    }
    String normalized = keyword.trim();
    NumberTemplate<Double> similarity =
        Expressions.numberTemplate(
            Double.class, "public.similarity({0}, {1})", subwayStationEntity.name, normalized);
    BooleanBuilder condition =
        new BooleanBuilder(
            subwayStationEntity
                .name
                .containsIgnoreCase(normalized)
                .or(similarity.gt(SIMILARITY_THRESHOLD)));
    return queryFactory
        .selectFrom(subwayStationEntity)
        .where(condition)
        .orderBy(similarity.desc())
        .limit(LIMIT)
        .fetch();
  }
}
