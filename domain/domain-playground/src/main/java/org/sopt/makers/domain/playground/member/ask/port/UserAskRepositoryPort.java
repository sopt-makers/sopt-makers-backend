package org.sopt.makers.domain.playground.member.ask.port;

import java.util.Optional;
import org.sopt.makers.domain.playground.member.ask.UserAsk;

public interface UserAskRepositoryPort {

  UserAsk save(UserAsk userAsk);

  Optional<UserAsk> findById(Long questionId);

  void deleteById(Long questionId);
}
