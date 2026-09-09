package org.sopt.makers.storage.db.playground.member.ask.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.member.ask.UserAnswer;
import org.sopt.makers.domain.playground.member.ask.port.UserAnswerRepositoryPort;
import org.sopt.makers.storage.db.playground.member.ask.entity.UserAnswerEntity;
import org.sopt.makers.storage.db.playground.member.ask.repository.UserAnswerJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAnswerRepositoryAdapter implements UserAnswerRepositoryPort {

  private final UserAnswerJpaRepository userAnswerJpaRepository;

  @Transactional
  @Override
  public UserAnswer save(UserAnswer userAnswer) {
    return userAnswerJpaRepository.save(UserAnswerEntity.fromDomain(userAnswer)).toDomain();
  }

  @Override
  public Optional<UserAnswer> findById(Long answerId) {
    return userAnswerJpaRepository.findById(answerId).map(UserAnswerEntity::toDomain);
  }

  @Transactional
  @Override
  public void deleteById(Long answerId) {
    userAnswerJpaRepository.deleteById(answerId);
  }

  @Transactional
  @Override
  public void deleteByAskId(Long askId) {
    userAnswerJpaRepository.deleteByAskId(askId);
  }

  @Override
  public boolean existsByAskId(Long askId) {
    return userAnswerJpaRepository.existsByQuestionId(askId);
  }

  @Override
  public Optional<UserAnswer> findByAskId(Long askId) {
    return userAnswerJpaRepository.findByQuestionId(askId).map(UserAnswerEntity::toDomain);
  }

  @Override
  public List<UserAnswer> findAllByAskIds(List<Long> askIds) {
    if (askIds.isEmpty()) {
      return List.of();
    }
    return userAnswerJpaRepository.findAllByQuestionIdIn(askIds).stream()
        .map(UserAnswerEntity::toDomain)
        .toList();
  }
}
