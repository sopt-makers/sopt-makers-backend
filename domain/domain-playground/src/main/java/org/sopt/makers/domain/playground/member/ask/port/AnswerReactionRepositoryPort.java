package org.sopt.makers.domain.playground.member.ask.port;

import java.util.Optional;
import org.sopt.makers.domain.playground.member.ask.AnswerReaction;

public interface AnswerReactionRepositoryPort {

  AnswerReaction save(AnswerReaction answerReaction);

  Optional<AnswerReaction> findById(Long reactionId);

  void deleteById(Long reactionId);
}
