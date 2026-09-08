package org.sopt.makers.storage.db.playground.member.ask.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.storage.db.playground.member.ask.entity.UserAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserAnswerJpaRepository extends JpaRepository<UserAnswerEntity, Long> {

  boolean existsByQuestionId(Long askId);

  Optional<UserAnswerEntity> findByQuestionId(Long askId);

  List<UserAnswerEntity> findAllByQuestionIdIn(List<Long> askIds);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("DELETE FROM UserAnswerEntity a WHERE a.questionId = :askId")
  void deleteByAskId(@Param("askId") Long askId);
}
