package org.sopt.makers.storage.db.playground.community.post.repository;

import org.sopt.makers.storage.db.playground.community.post.entity.DeletedPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeletedPostJpaRepository extends JpaRepository<DeletedPostEntity, Long> {}
