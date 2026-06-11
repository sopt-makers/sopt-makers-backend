package org.sopt.makers.api.common.config;

import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JwtSecretKeyConfig {

  private final SecurityProperty securityProperty;

  @Bean
  public SecretKey jwtSecretKey() {
    byte[] keyBytes = securityProperty.jwt().secret().secretKey().getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
