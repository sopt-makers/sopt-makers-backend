package org.sopt.makers.storage.db.playground.wordchaingame.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.wordchaingame.WordChainGameRoom;
import org.sopt.makers.domain.playground.wordchaingame.WordChainGameWinner;
import org.sopt.makers.storage.db.playground.wordchaingame.entity.QWordChainGameRoomEntity;
import org.sopt.makers.storage.db.playground.wordchaingame.entity.QWordChainGameWinnerEntity;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WordChainGameQueryRepository {

  private final JPAQueryFactory queryFactory;

  public List<WordChainGameRoom> findAllLimitedRooms(Integer limit, Long cursor) {
    QWordChainGameRoomEntity room = QWordChainGameRoomEntity.wordChainGameRoomEntity;
    return queryFactory
        .selectFrom(room)
        .where(ltRoomId(cursor))
        .orderBy(room.id.desc())
        .groupBy(room.id)
        .limit(limit)
        .fetch()
        .stream()
        .map(e -> e.toDomain())
        .toList();
  }

  public List<WordChainGameRoom> findAllRooms() {
    QWordChainGameRoomEntity room = QWordChainGameRoomEntity.wordChainGameRoomEntity;
    return queryFactory
        .selectFrom(room)
        .orderBy(room.id.desc())
        .groupBy(room.id)
        .fetch()
        .stream()
        .map(e -> e.toDomain())
        .toList();
  }

  public List<WordChainGameWinner> findAllLimitedWinners(Integer limit, Integer cursor) {
    QWordChainGameWinnerEntity winner = QWordChainGameWinnerEntity.wordChainGameWinnerEntity;
    return queryFactory
        .selectFrom(winner)
        .offset(cursor)
        .limit(limit)
        .orderBy(winner.id.desc())
        .fetch()
        .stream()
        .map(e -> e.toDomain())
        .toList();
  }

  private BooleanExpression ltRoomId(Long roomId) {
    QWordChainGameRoomEntity room = QWordChainGameRoomEntity.wordChainGameRoomEntity;
    if (roomId == null || roomId == 0) return null;
    return room.id.lt(roomId);
  }
}
