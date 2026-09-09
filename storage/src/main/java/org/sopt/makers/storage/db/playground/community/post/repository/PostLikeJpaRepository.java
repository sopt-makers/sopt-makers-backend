package org.sopt.makers.storage.db.playground.community.post.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.storage.db.playground.community.post.entity.PostLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeJpaRepository extends JpaRepository<PostLikeEntity, Long> {

  boolean existsByUserIdAndPostId(Long userId, Long postId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("DELETE FROM PostLikeEntity postLike WHERE postLike.postId = :postId")
  void deleteAllByPostId(@Param("postId") Long postId);

  Optional<PostLikeEntity> findByUserIdAndPostId(Long userId, Long postId);

  int countAllByPostId(Long postId);

  int countAllByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);

  @Query(
      """
      SELECT postLike.postId
      FROM PostLikeEntity postLike
      WHERE postLike.userId = :userId
        AND postLike.postId IN :postIds
      """)
  List<Long> findLikedPostIdsByUserIdAndPostIds(
      @Param("userId") Long userId, @Param("postIds") List<Long> postIds);

  @Query(
      """
      SELECT postLike.postId AS postId,
             COUNT(postLike) AS likeCount
      FROM PostLikeEntity postLike
      WHERE postLike.postId IN :postIds
      GROUP BY postLike.postId
      """)
  List<PostLikeCountProjection> countLikesByPostIds(@Param("postIds") List<Long> postIds);

  interface PostLikeCountProjection {
    Long getPostId();

    Long getLikeCount();
  }
}
