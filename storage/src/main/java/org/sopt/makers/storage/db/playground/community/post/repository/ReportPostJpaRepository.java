package org.sopt.makers.storage.db.playground.community.post.repository;

import org.sopt.makers.storage.db.playground.community.post.entity.ReportPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportPostJpaRepository extends JpaRepository<ReportPostEntity, Long> {}
