package org.sopt.makers.storage.db.playground.community.post.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.CommunityCategoryCode;
import org.sopt.makers.storage.db.playground.community.entity.QCategoryEntity;
import org.sopt.makers.storage.db.playground.community.post.entity.PostEntity;
import org.sopt.makers.storage.db.playground.community.post.entity.QPostEntity;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostQuerydslRepositoryImpl implements PostQuerydslRepository {

  private static final QPostEntity post = QPostEntity.postEntity;
  private static final QCategoryEntity category = QCategoryEntity.categoryEntity;
  private static final QCategoryEntity parentCategory = new QCategoryEntity("parentCategory");

  private final JPAQueryFactory queryFactory;

  @Override
  public List<PostEntity> findByCategoryCodesWithCursor(
      List<CommunityCategoryCode> categoryCodes,
      LocalDateTime cursorCreatedAt,
      Long cursorPostId,
      LocalDateTime snapshotTime,
      int limit) {
    JPAQuery<PostEntity> query =
        queryFactory
            .selectFrom(post)
            .innerJoin(post.category, category)
            .fetchJoin()
            .leftJoin(category.parent, parentCategory)
            .fetchJoin()
            .where(
                category.code.in(categoryCodes),
                post.createdAt.loe(snapshotTime),
                ltCursor(cursorCreatedAt, cursorPostId))
            .orderBy(post.createdAt.desc(), post.id.desc())
            .limit(limit);

    return query.fetch();
  }

  @Override
  public void updateIsHotByPostId(Long postId) {
    queryFactory.update(post).set(post.isHot, true).where(post.id.eq(postId)).execute();
  }

  private BooleanExpression ltCursor(LocalDateTime cursorCreatedAt, Long cursorPostId) {
    if (cursorCreatedAt == null || cursorPostId == null) {
      return null;
    }

    return post.createdAt
        .lt(cursorCreatedAt)
        .or(post.createdAt.eq(cursorCreatedAt).and(post.id.lt(cursorPostId)));
  }
}
