package org.sopt.makers.storage.db.playground.community.post.repository;

import org.sopt.makers.storage.db.playground.community.post.entity.ReportPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReportPostJpaRepository extends JpaRepository<ReportPostEntity, Long> {

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("DELETE FROM ReportPostEntity reportPost WHERE reportPost.postId = :postId")
  void deleteAllByPostId(@Param("postId") Long postId);
}
