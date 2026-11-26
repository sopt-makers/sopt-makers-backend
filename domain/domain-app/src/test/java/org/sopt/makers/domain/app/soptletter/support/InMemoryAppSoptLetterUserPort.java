package org.sopt.makers.domain.app.soptletter.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.Role;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.port.AppSoptLetterUserPort;

public final class InMemoryAppSoptLetterUserPort implements AppSoptLetterUserPort {

  private final Map<Long, User> users = new HashMap<>();

  public void add(User user) {
    users.put(user.id(), user);
  }

  @Override
  public Set<Long> findSoptUserIdsByGeneration(int generation) {
    return users.values().stream()
        .filter(
            user ->
                user.activities().activities().stream()
                    .anyMatch(a -> a.isSopt() && a.generation() == generation))
        .map(User::id)
        .collect(Collectors.toSet());
  }

  @Override
  public Optional<User> findWithActivities(Long userId) {
    return Optional.ofNullable(users.get(userId));
  }

  public static Activity soptActivity(int generation) {
    return Activity.of((long) generation, generation, null, null, Role.MEMBER, true, null);
  }
}
