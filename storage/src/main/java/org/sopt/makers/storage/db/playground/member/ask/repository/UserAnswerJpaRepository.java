package org.sopt.makers.storage.db.playground.member.ask.repository;

import org.sopt.makers.storage.db.playground.member.ask.entity.UserAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAnswerJpaRepository extends JpaRepository<UserAnswerEntity, Long> {

  boolean existsByQuestionId(Long questionId);
}
