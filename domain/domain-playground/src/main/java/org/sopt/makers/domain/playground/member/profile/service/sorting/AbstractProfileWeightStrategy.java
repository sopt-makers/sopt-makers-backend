package org.sopt.makers.domain.playground.member.profile.service.sorting;

import org.sopt.makers.domain.user.Profile;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.UserFavor;

abstract class AbstractProfileWeightStrategy implements ProfileWeightStrategy {

  private static final int INTRODUCTION_WEIGHT = 3;
  private static final int LINK_WEIGHT = 1;
  private static final int OTHER_FIELD_WEIGHT = 1;

  protected abstract int profileImageWeight();

  protected abstract int careerWeight();

  @Override
  public int calculate(User user) {
    Profile profile = user.profile();
    int weight = 0;

    if (hasText(profile.profileImage())) {
      weight += profileImageWeight();
    }
    if (profile.birthday() != null) {
      weight += OTHER_FIELD_WEIGHT;
    }
    if (hasText(profile.phone())) {
      weight += OTHER_FIELD_WEIGHT;
    }
    if (hasText(profile.email())) {
      weight += OTHER_FIELD_WEIGHT;
    }
    if (hasText(profile.introduction())) {
      weight += INTRODUCTION_WEIGHT;
    }
    if (hasText(profile.address())) {
      weight += OTHER_FIELD_WEIGHT;
    }
    if (hasText(profile.university())) {
      weight += OTHER_FIELD_WEIGHT;
    }
    if (hasText(profile.major())) {
      weight += OTHER_FIELD_WEIGHT;
    }
    if (hasText(profile.skill())) {
      weight += OTHER_FIELD_WEIGHT;
    }
    if (hasText(profile.mbti())) {
      weight += OTHER_FIELD_WEIGHT;
    }
    if (hasText(profile.mbtiDescription())) {
      weight += OTHER_FIELD_WEIGHT;
    }
    if (profile.sojuCapacity() != null) {
      weight += OTHER_FIELD_WEIGHT;
    }
    if (hasText(profile.interest())) {
      weight += OTHER_FIELD_WEIGHT;
    }
    if (hasText(profile.idealType())) {
      weight += OTHER_FIELD_WEIGHT;
    }
    if (hasText(profile.selfIntroduction())) {
      weight += OTHER_FIELD_WEIGHT;
    }

    UserFavor favor = profile.userFavor();
    if (favor != null) {
      if (favor.isPourSauceLover() != null) {
        weight += OTHER_FIELD_WEIGHT;
      }
      if (favor.isHardPeachLover() != null) {
        weight += OTHER_FIELD_WEIGHT;
      }
      if (favor.isMintChocoLover() != null) {
        weight += OTHER_FIELD_WEIGHT;
      }
      if (favor.isRedBeanFishBreadLover() != null) {
        weight += OTHER_FIELD_WEIGHT;
      }
      if (favor.isSojuLover() != null) {
        weight += OTHER_FIELD_WEIGHT;
      }
      if (favor.isRiceTteokLover() != null) {
        weight += OTHER_FIELD_WEIGHT;
      }
    }

    if (profile.links() != null) {
      weight += profile.links().size() * LINK_WEIGHT;
    }
    if (profile.careers() != null) {
      weight += profile.careers().size() * careerWeight();
    }

    return weight;
  }

  private boolean hasText(String value) {
    return value != null && !value.isBlank();
  }
}
