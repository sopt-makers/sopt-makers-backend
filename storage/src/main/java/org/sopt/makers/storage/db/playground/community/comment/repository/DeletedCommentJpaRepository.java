package org.sopt.makers.storage.db.playground.community.comment.repository;

import org.sopt.makers.storage.db.playground.community.comment.entity.DeletedCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeletedCommentJpaRepository extends JpaRepository<DeletedCommentEntity, Long> {}
