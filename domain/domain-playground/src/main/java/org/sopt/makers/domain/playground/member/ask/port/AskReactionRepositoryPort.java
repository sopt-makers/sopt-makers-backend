package org.sopt.makers.domain.playground.member.ask.port;

import java.util.Optional;
import org.sopt.makers.domain.playground.member.ask.AskReaction;

public interface AskReactionRepositoryPort {

  AskReaction save(AskReaction askReaction);

  Optional<AskReaction> findById(Long reactionId);

  void deleteById(Long reactionId);
}
