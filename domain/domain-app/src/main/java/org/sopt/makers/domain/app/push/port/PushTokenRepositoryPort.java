package org.sopt.makers.domain.app.push.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.push.PushToken;

public interface PushTokenRepositoryPort {

  boolean existsByUserIdAndToken(Long userId, String token);

  Optional<PushToken> findByUserIdAndToken(Long userId, String token);

  List<PushToken> findAllByUserId(Long userId);

  PushToken save(PushToken pushToken);

  void deleteById(Long id);
}
