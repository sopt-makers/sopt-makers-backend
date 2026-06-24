package org.sopt.makers.domain.admin.auth.port;

public interface AdminTokenIssuerPort {

  AdminTokenPair issue(Long adminId);

  AdminTokenPair refresh(String expiredAccessToken, String refreshToken);

  record AdminTokenPair(String accessToken, String refreshToken) {}
}
