package org.sopt.makers.storage.db.playground.member.ask.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.member.ask.AnswerReaction;
import org.sopt.makers.domain.playground.member.ask.port.AnswerReactionRepositoryPort;
import org.sopt.makers.storage.db.playground.member.ask.entity.AnswerReactionEntity;
import org.sopt.makers.storage.db.playground.member.ask.repository.AnswerReactionJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerReactionRepositoryAdapter implements AnswerReactionRepositoryPort {

  private final AnswerReactionJpaRepository answerReactionJpaRepository;

  @Transactional
  @Override
  public AnswerReaction save(AnswerReaction answerReaction) {
    return answerReactionJpaRepository.save(AnswerReactionEntity.fromDomain(answerReaction)).toDomain();
  }

  @Override
  public Optional<AnswerReaction> findById(Long reactionId) {
    return answerReactionJpaRepository.findById(reactionId).map(AnswerReactionEntity::toDomain);
  }

  @Transactional
  @Override
  public void deleteById(Long reactionId) {
    answerReactionJpaRepository.deleteById(reactionId);
  }

  @Override
  public Optional<AnswerReaction> findByAnswerIdAndReactorUserId(Long answerId, Long reactorUserId) {
    return answerReactionJpaRepository
        .findByAnswerIdAndReactorUserId(answerId, reactorUserId)
        .map(AnswerReactionEntity::toDomain);
  }
}
