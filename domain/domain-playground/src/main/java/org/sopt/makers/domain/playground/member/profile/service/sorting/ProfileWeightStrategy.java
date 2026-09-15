package org.sopt.makers.domain.playground.member.profile.service.sorting;

import org.sopt.makers.domain.user.User;

public interface ProfileWeightStrategy {
  int calculate(User user);
}
