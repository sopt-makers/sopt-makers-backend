package org.sopt.makers.storage.db.playground.post.repository;

import org.sopt.makers.storage.db.playground.post.entity.PostReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostReportJpaRepository extends JpaRepository<PostReportEntity, Long> {

  boolean existsByPostIdAndReporterId(Long postId, Long reporterId);
}
