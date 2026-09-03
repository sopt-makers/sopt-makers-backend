package org.sopt.makers.storage.db.playground.post.repository;

import org.sopt.makers.storage.db.playground.post.entity.PostCommentReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentReportJpaRepository
    extends JpaRepository<PostCommentReportEntity, Long> {

  boolean existsByCommentIdAndReporterId(Long commentId, Long reporterId);
}
