package org.sopt.makers.domain.app.soptamp.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.soptamp.port.SoptampProfileSourcePort;
import org.sopt.makers.domain.user.User;

public final class InMemoryProfileSource implements SoptampProfileSourcePort {

  private final List<User> store = new ArrayList<>();
  private Long failingUserId;

  public void save(User user) {
    store.add(user);
  }

  public void failChunkContaining(Long userId) {
    this.failingUserId = userId;
  }

  @Override
  public List<User> findAllForUpsert() {
    return List.copyOf(store);
  }

  @Override
  public List<User> findAllByUserIds(List<Long> userIds) {
    if (failingUserId != null && userIds.contains(failingUserId)) {
      throw new IllegalStateException("프로필 조회 실패");
    }
    return store.stream().filter(user -> userIds.contains(user.id())).toList();
  }

  @Override
  public Optional<User> findByUserId(Long userId) {
    return store.stream().filter(user -> user.id().equals(userId)).findFirst();
  }
}
