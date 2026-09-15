package org.sopt.makers.storage.db.playground.post.repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.storage.db.playground.post.entity.PostCommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostCommentJpaRepository extends JpaRepository<PostCommentEntity, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT comment FROM PostCommentEntity comment WHERE comment.id = :commentId")
  Optional<PostCommentEntity> findByIdForUpdate(@Param("commentId") Long commentId);

  Page<PostCommentEntity> findAllByPostIdAndParentCommentIdIsNull(Long postId, Pageable pageable);

  List<PostCommentEntity> findAllByParentCommentIdInOrderByParentCommentIdAscOrderAsc(
      List<Long> parentCommentIds);

  Optional<PostCommentEntity> findFirstByParentCommentIdOrderByOrderDesc(Long parentCommentId);

  @Query(
      "SELECT DISTINCT comment.writerId FROM PostCommentEntity comment "
          + "WHERE comment.postId = :postId AND comment.writerId IS NOT NULL "
          + "ORDER BY comment.writerId")
  List<Long> findDistinctWriterIdsByPostId(@Param("postId") Long postId);
}
