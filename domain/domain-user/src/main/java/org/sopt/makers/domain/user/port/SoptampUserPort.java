package org.sopt.makers.domain.user.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.user.User;

public interface SoptampUserPort {

  Optional<User> findWithActivitiesById(Long userId);

  List<User> findAllWithActivitiesByIds(List<Long> userIds);
}
