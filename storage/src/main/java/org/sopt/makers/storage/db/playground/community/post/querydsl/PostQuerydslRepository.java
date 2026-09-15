package org.sopt.makers.storage.db.playground.community.post.querydsl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.sopt.makers.domain.playground.community.CommunityCategoryCode;
import org.sopt.makers.storage.db.playground.community.post.entity.PostEntity;

public interface PostQuerydslRepository {

  List<PostEntity> findByCategoryCodesWithCursor(
      List<CommunityCategoryCode> categoryCodes,
      LocalDateTime cursorCreatedAt,
      Long cursorPostId,
      LocalDateTime snapshotTime,
      int limit,
      Set<Long> excludedWriterIds);

  void updateIsHotByPostId(Long postId);
}
