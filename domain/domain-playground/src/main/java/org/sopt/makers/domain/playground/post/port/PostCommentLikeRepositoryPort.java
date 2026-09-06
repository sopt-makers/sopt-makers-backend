package org.sopt.makers.domain.playground.post.port;

import java.util.List;
import java.util.Set;
import org.sopt.makers.domain.playground.post.like.PostCommentLike;

public interface PostCommentLikeRepositoryPort {

  PostCommentLike save(PostCommentLike like);

  boolean existsByCommentIdAndUserId(Long commentId, Long userId);

  void deleteByCommentIdAndUserId(Long commentId, Long userId);

  Set<Long> findLikedCommentIds(List<Long> commentIds, Long userId);

  void deleteAllByUserId(Long userId);
}
