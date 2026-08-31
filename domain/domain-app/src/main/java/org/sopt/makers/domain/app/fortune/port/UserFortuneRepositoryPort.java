package org.sopt.makers.domain.app.fortune.port;

import java.util.Optional;
import org.sopt.makers.domain.app.fortune.UserFortune;

public interface UserFortuneRepositoryPort {

  Optional<UserFortune> findByUserId(Long userId);

  UserFortune save(UserFortune userFortune);
}
