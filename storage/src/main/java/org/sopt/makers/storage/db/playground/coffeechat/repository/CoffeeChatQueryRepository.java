package org.sopt.makers.storage.db.playground.coffeechat.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.coffeechat.port.CoffeeChatRepositoryPort.HistoryInfo;
import org.sopt.makers.storage.db.playground.coffeechat.entity.CoffeeChatEntity;
import org.sopt.makers.storage.db.playground.coffeechat.entity.QCoffeeChatEntity;
import org.sopt.makers.storage.db.playground.coffeechat.entity.QCoffeeChatHistoryEntity;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CoffeeChatQueryRepository {

  private final JPAQueryFactory queryFactory;

  public List<HistoryInfo> findCoffeeChatHistoryTitles(Long senderId) {
    QCoffeeChatHistoryEntity history = QCoffeeChatHistoryEntity.coffeeChatHistoryEntity;
    QCoffeeChatEntity coffeeChat = QCoffeeChatEntity.coffeeChatEntity;

    List<CoffeeChatEntity> results =
        queryFactory
            .selectFrom(coffeeChat)
            .join(history)
            .on(coffeeChat.memberId.eq(history.receiverId))
            .where(history.senderId.eq(senderId))
            .orderBy(history.createdAt.desc())
            .fetch();

    return results.stream()
        .map(
            cc ->
                new HistoryInfo(
                    cc.getId(),
                    cc.getCoffeeChatBio(),
                    cc.getMemberId(),
                    cc.getCareer(),
                    cc.getCoffeeChatTopicTypes()))
        .toList();
  }
}
