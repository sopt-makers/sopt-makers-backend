package org.sopt.makers.storage.db.app.soptletter.querydsl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.storage.db.app.soptletter.entity.QSoptLetterEntity;
import org.sopt.makers.storage.db.app.soptletter.entity.QSoptLetterTopicEntity;
import org.sopt.makers.storage.db.app.soptletter.entity.QSoptLetterTopicReadEntity;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SoptLetterQuerydslRepository {

  private final JPAQueryFactory queryFactory;

  public List<TopicUnreadCount> countUnreadByTopic(
      Long userId, Long excludedAuthorProfileId, LocalDateTime now) {
    QSoptLetterEntity letter = QSoptLetterEntity.soptLetterEntity;
    QSoptLetterTopicEntity topic = QSoptLetterTopicEntity.soptLetterTopicEntity;
    QSoptLetterTopicReadEntity read = QSoptLetterTopicReadEntity.soptLetterTopicReadEntity;

    return queryFactory
        .select(Projections.constructor(TopicUnreadCount.class, letter.topicId, letter.count()))
        .from(letter)
        .join(topic)
        .on(topic.id.eq(letter.topicId))
        .leftJoin(read)
        .on(read.topicId.eq(letter.topicId), read.userId.eq(userId))
        .where(
            topic.startedAt.loe(now),
            topic.endedAt.goe(now),
            read.id.isNull().or(letter.id.gt(read.lastReadLetterId)),
            notWrittenBy(letter, excludedAuthorProfileId))
        .groupBy(letter.topicId)
        .fetch();
  }

  private BooleanExpression notWrittenBy(QSoptLetterEntity letter, Long authorProfileId) {
    if (authorProfileId == null) {
      return null;
    }
    return letter.authorProfileId.ne(authorProfileId);
  }
}
