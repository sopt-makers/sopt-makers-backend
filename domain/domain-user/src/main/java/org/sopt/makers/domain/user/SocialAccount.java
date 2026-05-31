package org.sopt.makers.domain.user;

import static org.sopt.makers.domain.user.exception.UserFailure.INVALID_SOCIAL_ACCOUNT;

import org.sopt.makers.core.type.OAuthPlatform;
import org.sopt.makers.domain.user.exception.UserException;

public record SocialAccount(String authPlatformId, OAuthPlatform authPlatformType) {

  public static SocialAccount of(String authPlatformId, OAuthPlatform authPlatformType) {
    if (authPlatformId == null || authPlatformId.isBlank()) {
      throw new UserException(INVALID_SOCIAL_ACCOUNT);
    }
    if (authPlatformType == null) {
      throw new UserException(INVALID_SOCIAL_ACCOUNT);
    }
    return new SocialAccount(authPlatformId, authPlatformType);
  }
}
