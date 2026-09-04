package org.sopt.makers.storage.db.playground.community.comment.repository;

import java.util.List;
import org.sopt.makers.storage.db.playground.community.comment.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long> {

  List<CommentEntity> findAllByPostIdOrderByIdAsc(Long postId);

  List<CommentEntity> findAllByPostIdInOrderByPostIdAscIdAsc(List<Long> postIds);

  List<CommentEntity> findAllByParentCommentId(Long parentCommentId);

  @Query(
      """
      SELECT comment.postId AS postId,
             COUNT(comment) AS commentCount
      FROM CommentEntity comment
      WHERE comment.postId IN :postIds
        AND comment.isDeleted = false
      GROUP BY comment.postId
      """)
  List<CommentCountProjection> countNonDeletedByPostIds(@Param("postIds") List<Long> postIds);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("DELETE FROM CommentEntity comment WHERE comment.postId = :postId")
  void deleteAllByPostId(@Param("postId") Long postId);

  interface CommentCountProjection {
    Long getPostId();

    Long getCommentCount();
  }
}
