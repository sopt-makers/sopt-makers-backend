package org.sopt.makers.domain.playground.community.post.port;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.playground.community.CommunityCategoryCode;
import org.sopt.makers.domain.playground.community.CommunityCategoryGroup;
import org.sopt.makers.domain.playground.community.post.Post;

public interface PostRepositoryPort {

  Post save(Post post);

  void delete(Post post);

  boolean existsById(Long postId);

  Optional<Post> findById(Long postId);

  Optional<Post> findByIdWithCategory(Long postId);

  List<Post> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

  Integer countAllByWriterIdAndCreatedAtBetween(Long writerId, LocalDateTime start, LocalDateTime end);

  void increaseHits(Long postId);

  List<Post> findTop5ByCategoryCodesOrderByCreatedAtDesc(List<CommunityCategoryCode> categoryCodes);

  List<Post> findTop3ByCategoryGroupsOrderByCreatedAtDesc(List<CommunityCategoryGroup> categoryGroups);

  Optional<Post> findFirstByCategoryCodesOrderByCreatedAtDesc(List<CommunityCategoryCode> categoryCodes);

  long countSopticleByWriterId(Long writerId);

  List<Post> findByCategoryCodesWithCursor(
      List<CommunityCategoryCode> categoryCodes,
      LocalDateTime cursorCreatedAt,
      Long cursorPostId,
      LocalDateTime snapshotTime,
      int limit);

  Optional<Post> findMostRecentHotPost();

  void markAsHot(Long postId);

  List<Post> findPopularPosts(int limitCount);

  List<Post> findPopularCandidatePosts(LocalDateTime since);
}
