package org.sopt.makers.domain.user.port;

import java.util.List;
import java.util.Map;
import org.sopt.makers.domain.user.User;

public interface UserCacheRepositoryPort {

  Map<Long, User> getAllPresent(List<Long> userIds);

  void put(User user);

  void evict(Long userId);
}
