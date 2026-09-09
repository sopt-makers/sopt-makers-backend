package org.sopt.makers.domain.playground.member.ask.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.playground.member.ask.UserAnswer;

public interface UserAnswerRepositoryPort {

  UserAnswer save(UserAnswer userAnswer);

  Optional<UserAnswer> findById(Long answerId);

  void deleteById(Long answerId);

  void deleteByAskId(Long askId);

  boolean existsByAskId(Long askId);

  Optional<UserAnswer> findByAskId(Long askId);

  List<UserAnswer> findAllByAskIds(List<Long> askIds);
}
