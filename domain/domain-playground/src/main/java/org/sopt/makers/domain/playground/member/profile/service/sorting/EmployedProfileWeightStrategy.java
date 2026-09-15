package org.sopt.makers.domain.playground.member.profile.service.sorting;

import org.springframework.stereotype.Component;

@Component
public class EmployedProfileWeightStrategy extends AbstractProfileWeightStrategy {

  @Override
  protected int profileImageWeight() {
    return 3;
  }

  @Override
  protected int careerWeight() {
    return 5;
  }
}
