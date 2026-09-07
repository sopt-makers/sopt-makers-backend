package org.sopt.makers.domain.playground.member.ask.port;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.sopt.makers.domain.playground.member.ask.AskReaction;

public interface AskReactionRepositoryPort {

  AskReaction save(AskReaction askReaction);

  Optional<AskReaction> findById(Long reactionId);

  void deleteById(Long reactionId);

  void deleteAllByQuestionId(Long questionId);

  Optional<AskReaction> findByQuestionIdAndReactorUserId(Long questionId, Long reactorUserId);

  Map<Long, Long> countGroupedByQuestionIds(List<Long> questionIds);

  Set<Long> findReactedQuestionIdsByUser(List<Long> questionIds, Long reactorUserId);
}
