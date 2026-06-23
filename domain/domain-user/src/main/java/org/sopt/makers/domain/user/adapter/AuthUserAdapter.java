package org.sopt.makers.domain.user.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.OAuthPlatform;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.Profile;
import org.sopt.makers.domain.user.SocialAccount;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.port.AuthUserPort;
import org.sopt.makers.domain.user.service.UserCommandService;
import org.sopt.makers.domain.user.service.UserQueryService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUserAdapter implements AuthUserPort {

  private final UserQueryService userQueryService;
  private final UserCommandService userCommandService;

  @Override
  public Optional<User> findBySocialAccount(OAuthPlatform platform, String platformId) {
    return userQueryService.findBySocialAccount(platform, platformId);
  }

  @Override
  public User getByPhone(String phone) {
    return userQueryService.getByPhone(phone);
  }

  @Override
  public User getById(Long userId) {
    return userQueryService.getById(userId);
  }

  @Override
  public boolean existsByPhone(String phone) {
    return userQueryService.existsByPhone(phone);
  }

  @Override
  public User createUser(SocialAccount socialAccount, Profile profile) {
    return userCommandService.createUser(socialAccount, profile);
  }

  @Override
  public void addActivity(Long userId, Activity activity) {
    userCommandService.addActivity(userId, activity);
  }

  @Override
  public void completeFirstLogin(Long userId) {
    userCommandService.completeFirstLogin(userId);
  }

  @Override
  public void updateSocialAccount(Long userId, SocialAccount socialAccount) {
    userCommandService.updateSocialAccount(userId, socialAccount);
  }
}
