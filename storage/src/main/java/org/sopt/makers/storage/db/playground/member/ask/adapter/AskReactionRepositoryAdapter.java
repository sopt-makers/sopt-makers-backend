package org.sopt.makers.storage.db.playground.member.ask.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.member.ask.AskReaction;
import org.sopt.makers.domain.playground.member.ask.port.AskReactionRepositoryPort;
import org.sopt.makers.storage.db.playground.member.ask.entity.AskReactionEntity;
import org.sopt.makers.storage.db.playground.member.ask.repository.AskReactionJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AskReactionRepositoryAdapter implements AskReactionRepositoryPort {

  private final AskReactionJpaRepository askReactionJpaRepository;

  @Transactional
  @Override
  public AskReaction save(AskReaction askReaction) {
    return askReactionJpaRepository.save(AskReactionEntity.fromDomain(askReaction)).toDomain();
  }

  @Override
  public Optional<AskReaction> findById(Long reactionId) {
    return askReactionJpaRepository.findById(reactionId).map(AskReactionEntity::toDomain);
  }

  @Transactional
  @Override
  public void deleteById(Long reactionId) {
    askReactionJpaRepository.deleteById(reactionId);
  }

  @Override
  public Optional<AskReaction> findByQuestionIdAndReactorUserId(Long questionId, Long reactorUserId) {
    return askReactionJpaRepository
        .findByQuestionIdAndReactorUserId(questionId, reactorUserId)
        .map(AskReactionEntity::toDomain);
  }
}
