package org.sopt.makers.storage.db.playground.community.comment.repository;

import java.util.List;
import org.sopt.makers.storage.db.playground.community.comment.entity.ReportCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReportCommentJpaRepository extends JpaRepository<ReportCommentEntity, Long> {

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("DELETE FROM ReportCommentEntity reportComment WHERE reportComment.commentId IN :commentIds")
  void deleteAllByCommentIdIn(@Param("commentIds") List<Long> commentIds);
}
