package org.sopt.makers.storage.db.playground.member.ask.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.storage.db.playground.member.ask.entity.UserAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserAnswerJpaRepository extends JpaRepository<UserAnswerEntity, Long> {

  boolean existsByQuestionId(Long questionId);

  Optional<UserAnswerEntity> findByQuestionId(Long questionId);

  List<UserAnswerEntity> findAllByQuestionIdIn(List<Long> questionIds);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("DELETE FROM UserAnswerEntity a WHERE a.questionId = :questionId")
  void deleteByQuestionId(@Param("questionId") Long questionId);
}
