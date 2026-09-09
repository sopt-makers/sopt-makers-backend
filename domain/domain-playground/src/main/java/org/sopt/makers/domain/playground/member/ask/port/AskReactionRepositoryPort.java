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

  void deleteAllByAskId(Long askId);

  Optional<AskReaction> findByAskIdAndReactorUserId(Long askId, Long reactorUserId);

  Map<Long, Long> countGroupedByAskIds(List<Long> askIds);

  Set<Long> findReactedAskIdsByUser(List<Long> askIds, Long reactorUserId);
}
