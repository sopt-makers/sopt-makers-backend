package org.sopt.makers.domain.playground.member.ask.port;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.sopt.makers.domain.playground.member.ask.AnswerReaction;

public interface AnswerReactionRepositoryPort {

  AnswerReaction save(AnswerReaction answerReaction);

  Optional<AnswerReaction> findById(Long reactionId);

  void deleteById(Long reactionId);

  void deleteAllByAnswerId(Long answerId);

  Optional<AnswerReaction> findByAnswerIdAndReactorUserId(Long answerId, Long reactorUserId);

  Map<Long, Long> countGroupedByAnswerIds(List<Long> answerIds);

  Set<Long> findReactedAnswerIdsByUser(List<Long> answerIds, Long reactorUserId);
}
