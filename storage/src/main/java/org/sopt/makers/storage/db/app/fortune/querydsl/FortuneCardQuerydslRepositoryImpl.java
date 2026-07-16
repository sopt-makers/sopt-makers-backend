package org.sopt.makers.storage.db.app.fortune.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.storage.db.app.fortune.entity.FortuneCardEntity;
import org.sopt.makers.storage.db.app.fortune.entity.QFortuneCardEntity;
import org.sopt.makers.storage.db.app.fortune.entity.QFortuneWordEntity;
import org.sopt.makers.storage.db.app.fortune.entity.QUserFortuneEntity;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FortuneCardQuerydslRepositoryImpl implements FortuneCardQuerydslRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public Optional<FortuneCardEntity> findTodayCardByUserId(Long userId) {
    QUserFortuneEntity userFortune = QUserFortuneEntity.userFortuneEntity;
    QFortuneWordEntity fortuneWord = QFortuneWordEntity.fortuneWordEntity;
    QFortuneCardEntity fortuneCard = QFortuneCardEntity.fortuneCardEntity;

    return Optional.ofNullable(
        queryFactory
            .select(fortuneCard)
            .from(userFortune, fortuneWord, fortuneCard)
            .where(
                userFortune.userId.eq(userId),
                userFortune.fortuneWordId.eq(fortuneWord.id),
                fortuneWord.fortuneCardId.eq(fortuneCard.id))
            .fetchFirst());
  }
}
