package org.sopt.makers.domain.playground.post.port;

import java.util.List;
import java.util.Set;
import org.sopt.makers.domain.playground.post.like.PostLike;

public interface PostLikeRepositoryPort {

  PostLike save(PostLike like);

  boolean existsByPostIdAndUserId(Long postId, Long userId);

  void deleteByPostIdAndUserId(Long postId, Long userId);

  Set<Long> findLikedPostIds(List<Long> postIds, Long userId);

  void deleteAllByUserId(Long userId);
}
