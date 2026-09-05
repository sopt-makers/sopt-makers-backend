package org.sopt.makers.storage.db.playground.member.ask.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.storage.db.playground.member.ask.entity.UserAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAnswerJpaRepository extends JpaRepository<UserAnswerEntity, Long> {

  boolean existsByQuestionId(Long questionId);

  Optional<UserAnswerEntity> findByQuestionId(Long questionId);

  List<UserAnswerEntity> findAllByQuestionIdIn(List<Long> questionIds);
}
