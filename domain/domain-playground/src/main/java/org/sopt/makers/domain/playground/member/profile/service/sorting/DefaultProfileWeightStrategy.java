package org.sopt.makers.domain.playground.member.profile.service.sorting;

import org.springframework.stereotype.Component;

@Component
public class DefaultProfileWeightStrategy extends AbstractProfileWeightStrategy {

  @Override
  protected int profileImageWeight() {
    return 5;
  }

  @Override
  protected int careerWeight() {
    return 3;
  }
}
