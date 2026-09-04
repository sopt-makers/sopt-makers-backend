package org.sopt.makers.storage.db.playground.community.comment.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.storage.db.playground.community.comment.entity.CommentLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentLikeJpaRepository extends JpaRepository<CommentLikeEntity, Long> {

  boolean existsByMemberIdAndCommentId(Long memberId, Long commentId);

  Optional<CommentLikeEntity> findByMemberIdAndCommentId(Long memberId, Long commentId);

  int countAllByCommentId(Long commentId);

  @Query(
      """
      SELECT commentLike.commentId
      FROM CommentLikeEntity commentLike
      WHERE commentLike.memberId = :memberId
        AND commentLike.commentId IN :commentIds
      """)
  List<Long> findLikedCommentIdsByMemberIdAndCommentIds(
      @Param("memberId") Long memberId, @Param("commentIds") List<Long> commentIds);

  @Query(
      """
      SELECT commentLike.commentId AS commentId,
             COUNT(commentLike) AS likeCount
      FROM CommentLikeEntity commentLike
      WHERE commentLike.commentId IN :commentIds
      GROUP BY commentLike.commentId
      """)
  List<CommentLikeCountProjection> countLikesByCommentIds(@Param("commentIds") List<Long> commentIds);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("DELETE FROM CommentLikeEntity commentLike WHERE commentLike.commentId IN :commentIds")
  void deleteAllByCommentIdIn(@Param("commentIds") List<Long> commentIds);

  interface CommentLikeCountProjection {
    Long getCommentId();

    Long getLikeCount();
  }
}
