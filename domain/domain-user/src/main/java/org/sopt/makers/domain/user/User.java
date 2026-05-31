package org.sopt.makers.domain.user;

public record User(
    Long id,
    Profile profile,
    SocialAccount socialAccount,
    ActivityList activities,
    boolean isFirstLogin) {
  public static User createNewUser(SocialAccount socialAccount, Profile profile) {
    return new User(null, profile, socialAccount, new ActivityList(), true);
  }

  public static User createUser(
      Long id, SocialAccount socialAccount, Profile profile, boolean isFirstLogin) {
    return new User(id, profile, socialAccount, new ActivityList(), isFirstLogin);
  }

  public static User createUser(
      Long id,
      SocialAccount socialAccount,
      Profile profile,
      ActivityList activities,
      boolean isFirstLogin) {
    return new User(id, profile, socialAccount, activities, isFirstLogin);
  }

  public User updateSocialAccount(SocialAccount socialAccount) {
    return new User(id, profile, socialAccount, activities, isFirstLogin);
  }

  public User updateProfile(Profile profile) {
    return new User(id, profile, socialAccount, activities, isFirstLogin);
  }

  public User completeFirstLogin() {
    return new User(id, profile, socialAccount, activities, false);
  }
}
