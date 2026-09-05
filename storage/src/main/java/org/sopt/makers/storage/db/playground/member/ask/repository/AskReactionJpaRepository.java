package org.sopt.makers.storage.db.playground.member.ask.repository;

import java.util.Optional;
import org.sopt.makers.storage.db.playground.member.ask.entity.AskReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AskReactionJpaRepository extends JpaRepository<AskReactionEntity, Long> {

  Optional<AskReactionEntity> findByQuestionIdAndReactorUserId(Long questionId, Long reactorUserId);
}
