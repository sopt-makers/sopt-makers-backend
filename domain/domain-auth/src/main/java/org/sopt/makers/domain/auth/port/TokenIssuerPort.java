package org.sopt.makers.domain.auth.port;

import org.sopt.makers.domain.user.Role;

public interface TokenIssuerPort {

  TokenPair issue(Long userId, Role role);

  TokenPair refresh(String expiredAccessToken, String refreshToken);

  record TokenPair(String accessToken, String refreshToken) {}
}
