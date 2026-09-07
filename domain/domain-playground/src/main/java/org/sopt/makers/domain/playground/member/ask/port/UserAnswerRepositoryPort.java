package org.sopt.makers.domain.playground.member.ask.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.playground.member.ask.UserAnswer;

public interface UserAnswerRepositoryPort {

  UserAnswer save(UserAnswer userAnswer);

  Optional<UserAnswer> findById(Long answerId);

  void deleteById(Long answerId);

  void deleteByQuestionId(Long questionId);

  boolean existsByQuestionId(Long questionId);

  Optional<UserAnswer> findByQuestionId(Long questionId);

  List<UserAnswer> findAllByQuestionIds(List<Long> questionIds);
}
