package org.sopt.makers.api.common.security.notice;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.sopt.makers.domain.crew.notice.port.NoticeAuthorizerPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NoticeSecretAuthorizerAdapter implements NoticeAuthorizerPort {

  private final String configuredSecretKey;

  public NoticeSecretAuthorizerAdapter(@Value("${crew.apikey:}") String configuredSecretKey) {
    this.configuredSecretKey = configuredSecretKey;
  }

  @Override
  public boolean isAuthorized(String secretKey) {
    if (configuredSecretKey.isBlank() || secretKey == null) {
      return false;
    }
    return MessageDigest.isEqual(
        configuredSecretKey.getBytes(StandardCharsets.UTF_8),
        secretKey.getBytes(StandardCharsets.UTF_8));
  }
}
