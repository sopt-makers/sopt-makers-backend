package org.sopt.makers.storage.db.playground.member.ask.adapter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.member.ask.AnswerReaction;
import org.sopt.makers.domain.playground.member.ask.port.AnswerReactionRepositoryPort;
import org.sopt.makers.storage.db.playground.member.ask.entity.AnswerReactionEntity;
import org.sopt.makers.storage.db.playground.member.ask.projection.AnswerReactionCountRow;
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

  @Override
  public Map<Long, Long> countGroupedByAnswerIds(List<Long> answerIds) {
    if (answerIds.isEmpty()) {
      return Map.of();
    }
    return answerReactionJpaRepository.countGroupedByAnswerIds(answerIds).stream()
        .collect(Collectors.toMap(AnswerReactionCountRow::answerId, AnswerReactionCountRow::count));
  }

  @Override
  public Set<Long> findReactedAnswerIdsByUser(List<Long> answerIds, Long reactorUserId) {
    if (answerIds.isEmpty()) {
      return Set.of();
    }
    return new HashSet<>(answerReactionJpaRepository.findReactedAnswerIds(answerIds, reactorUserId));
  }
}
