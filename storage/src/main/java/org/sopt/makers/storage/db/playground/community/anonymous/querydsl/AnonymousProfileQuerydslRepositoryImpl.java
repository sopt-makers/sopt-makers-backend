package org.sopt.makers.storage.db.playground.community.anonymous.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.storage.db.playground.community.anonymous.entity.AnonymousProfileEntity;
import org.sopt.makers.storage.db.playground.community.anonymous.entity.QAnonymousProfileEntity;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AnonymousProfileQuerydslRepositoryImpl implements AnonymousProfileQuerydslRepository {

  private static final QAnonymousProfileEntity anonymousProfile =
      QAnonymousProfileEntity.anonymousProfileEntity;

  private final JPAQueryFactory queryFactory;

  @Override
  public List<AnonymousProfileEntity> findRecentOrderByCreatedAtDesc(int limit) {
    return queryFactory
        .selectFrom(anonymousProfile)
        .orderBy(anonymousProfile.createdAt.desc())
        .limit(limit)
        .fetch();
  }
}
