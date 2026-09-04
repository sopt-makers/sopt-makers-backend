package org.sopt.makers.domain.playground.community.post.port;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.sopt.makers.domain.playground.community.post.PostLike;

public interface PostLikeRepositoryPort {

  PostLike save(PostLike postLike);

  void delete(PostLike postLike);

  boolean existsByUserIdAndPostId(Long userId, Long postId);

  Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId);

  int countAllByPostId(Long postId);

  int countAllByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);

  List<Long> findLikedPostIdsByUserIdAndPostIds(Long userId, List<Long> postIds);

  Map<Long, Long> countLikesByPostIds(List<Long> postIds);
}
