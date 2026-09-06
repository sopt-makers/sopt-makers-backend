package org.sopt.makers.storage.db.playground.post.repository;

import java.util.List;
import org.sopt.makers.storage.db.playground.post.entity.PostCommentLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentLikeJpaRepository extends JpaRepository<PostCommentLikeEntity, Long> {

  boolean existsByCommentIdAndUserId(Long commentId, Long userId);

  void deleteByCommentIdAndUserId(Long commentId, Long userId);

  List<PostCommentLikeEntity> findAllByCommentIdInAndUserId(List<Long> commentIds, Long userId);

  void deleteAllByUserId(Long userId);
}
