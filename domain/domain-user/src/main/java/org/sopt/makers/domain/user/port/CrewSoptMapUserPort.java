package org.sopt.makers.domain.user.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.user.User;

public interface CrewSoptMapUserPort {

  Optional<User> findById(Long userId);

  List<User> findAllByIds(List<Long> userIds);
}
