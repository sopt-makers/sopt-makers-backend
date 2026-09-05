package org.sopt.makers.storage.db.playground.member.ask.repository;

import java.util.Optional;
import org.sopt.makers.storage.db.playground.member.ask.entity.AnswerReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerReactionJpaRepository extends JpaRepository<AnswerReactionEntity, Long> {

  Optional<AnswerReactionEntity> findByAnswerIdAndReactorUserId(Long answerId, Long reactorUserId);
}
