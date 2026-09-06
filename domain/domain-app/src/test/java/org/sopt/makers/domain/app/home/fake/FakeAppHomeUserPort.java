package org.sopt.makers.domain.app.home.fake;

import static org.sopt.makers.domain.user.exception.UserFailure.NOT_FOUND_USER;

import java.util.HashMap;
import java.util.Map;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.exception.UserException;
import org.sopt.makers.domain.user.port.AppHomeUserPort;

public final class FakeAppHomeUserPort implements AppHomeUserPort {

  private final Map<Long, User> store = new HashMap<>();

  public void add(User user) {
    store.put(user.id(), user);
  }

  @Override
  public User getWithActivities(Long userId) {
    User user = store.get(userId);
    if (user == null) {
      throw new UserException(NOT_FOUND_USER);
    }
    return user;
  }
}
