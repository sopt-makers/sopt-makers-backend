package org.sopt.makers.domain.app.soptamp.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.user.User;

public interface SoptampProfileSourcePort {

  List<User> findAllForUpsert();

  List<User> findAllByUserIds(List<Long> userIds);

  Optional<User> findByUserId(Long userId);
}
