package org.sopt.makers.domain.user.port;

import java.util.Optional;
import org.sopt.makers.core.type.OAuthPlatform;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.Profile;
import org.sopt.makers.domain.user.SocialAccount;
import org.sopt.makers.domain.user.User;

public interface AuthUserPort {

  Optional<User> findBySocialAccount(OAuthPlatform platform, String platformId);

  User getByPhone(String phone);

  User getById(Long userId);

  boolean existsByPhone(String phone);

  User createUser(SocialAccount socialAccount, Profile profile);

  void addActivity(Long userId, Activity activity);

  void completeFirstLogin(Long userId);

  void updateSocialAccount(Long userId, SocialAccount socialAccount);
}
