package org.sopt.makers.storage.db.app.poke.querydsl;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.storage.db.app.poke.entity.PokeHistoryEntity;
import org.sopt.makers.storage.db.app.poke.entity.QPokeHistoryEntity;
import org.sopt.makers.storage.db.user.entity.QUserEntity;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PokeHistoryQuerydslRepositoryImpl implements PokeHistoryQuerydslRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public Optional<PokeHistoryEntity> findRandomUnRepliedPokeMe(Long userId) {
    QPokeHistoryEntity pokeHistory = QPokeHistoryEntity.pokeHistoryEntity;
    QPokeHistoryEntity latest = new QPokeHistoryEntity("latest");
    QUserEntity user = QUserEntity.userEntity;

    return Optional.ofNullable(
        queryFactory
            .selectFrom(pokeHistory)
            .where(
                pokeHistory.id.in(
                    JPAExpressions.select(latest.id.max())
                        .from(latest)
                        .where(
                            latest.pokedId.eq(userId),
                            latest.isReply.isFalse(),
                            JPAExpressions.selectOne()
                                .from(user)
                                .where(user.id.eq(latest.pokerId))
                                .exists())
                        .groupBy(latest.pokerId)))
            .orderBy(Expressions.numberTemplate(Double.class, "random()").asc())
            .fetchFirst());
  }
}
