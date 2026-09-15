package org.sopt.makers.domain.user.port;

import org.sopt.makers.domain.user.User;

public interface AppHomeUserPort {

  User getWithActivities(Long userId);
}
