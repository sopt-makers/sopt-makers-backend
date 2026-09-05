package org.sopt.makers.storage.db.playground.member.ask.repository;

import org.sopt.makers.storage.db.playground.member.ask.entity.AskReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AskReactionJpaRepository extends JpaRepository<AskReactionEntity, Long> {}
