package org.sopt.makers.clients.oauth;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.clients.oauth.apple.AppleOAuthService;
import org.sopt.makers.clients.oauth.exception.OAuthException;
import org.sopt.makers.clients.oauth.exception.OAuthFailure;
import org.sopt.makers.clients.oauth.google.GoogleOAuthService;
import org.sopt.makers.core.type.OAuthPlatform;
import org.sopt.makers.domain.auth.exception.AuthException;
import org.sopt.makers.domain.auth.exception.AuthFailure;
import org.sopt.makers.domain.auth.port.OAuthAuthenticatorPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuthAuthenticatorAdapter implements OAuthAuthenticatorPort {

  private final AppleOAuthService appleOAuthService;
  private final GoogleOAuthService googleOAuthService;

  @Override
  public String getIdentifier(String idToken, OAuthPlatform platform) {
    try {
      return switch (platform) {
        case APPLE -> appleOAuthService.getIdentifierByToken(idToken);
        case GOOGLE -> googleOAuthService.getIdentifierByToken(idToken);
        default -> throw new AuthException(AuthFailure.INVALID_SOCIAL_PLATFORM);
      };
    } catch (OAuthException e) {
      if (e.getError() == OAuthFailure.INVALID_ID_TOKEN) {
        throw new AuthException(AuthFailure.INVALID_ID_TOKEN);
      }
      throw e;
    }
  }
}
