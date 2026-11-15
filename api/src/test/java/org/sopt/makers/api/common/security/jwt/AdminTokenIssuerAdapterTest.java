package org.sopt.makers.api.common.security.jwt;

import static org.assertj.core.api.Assertions.*;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.api.common.config.SecurityProperty;
import org.sopt.makers.api.common.security.exception.TokenException;
import org.sopt.makers.domain.admin.auth.port.AdminRefreshTokenRepositoryPort;

class AdminTokenIssuerAdapterTest {
  private final SecretKey key =
      Keys.hmacShaKeyFor("01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8));
  private final SecurityProperty property =
      new SecurityProperty(
          null,
          new SecurityProperty.Jwt(
              new SecurityProperty.Jwt.Secret(
                  new SecurityProperty.Jwt.Secret.Expiration(3600L, 1209600L),
                  "unused",
                  new SecurityProperty.Jwt.Secret.Issuer("test-issuer"))));
  private final JwtAccessTokenService access = new JwtAccessTokenService(key, property);
  private final JwtRefreshTokenService refresh = new JwtRefreshTokenService(key, property);
  private final Tokens repository = new Tokens();
  private final AdminTokenIssuerAdapter adapter =
      new AdminTokenIssuerAdapter(new JwtTokenRotator(access, refresh), repository);

  @Test
  @DisplayName("갱신 토큰만으로 같은 관리자 ID와 권한의 토큰을 발급하고 기존 토큰을 폐기한다")
  void rotatesWithoutAccessToken() {
    var old = adapter.issue(1L);
    var next = adapter.refresh(old.refreshToken());
    var authentication = access.parse("Bearer " + next.accessToken());
    assertThat(authentication.getPrincipal()).isEqualTo("1");
    assertThat(authentication.getAuthorities())
        .extracting(a -> a.getAuthority())
        .containsExactly("ADMIN");
    assertThat(next.refreshToken()).isNotEqualTo(old.refreshToken());
    assertThatThrownBy(() -> adapter.refresh(old.refreshToken()))
        .isInstanceOf(TokenException.class);
    assertThat(adapter.refresh(next.refreshToken()).refreshToken())
        .isNotEqualTo(next.refreshToken());
  }

  @Test
  @DisplayName("일반 사용자용 갱신 토큰으로 관리자 토큰을 발급할 수 없다")
  void rejectsOtherTokenType() {
    var token = refresh.generateJwt("1", "USER");
    repository.save(1L, token, Duration.ofDays(14));
    assertThatThrownBy(() -> adapter.refresh(token)).isInstanceOf(TokenException.class);
    assertThat(repository.tokens).contains("1:" + token);
  }

  @Test
  @DisplayName("서명은 유효해도 저장되지 않은 갱신 토큰은 거부한다")
  void rejectsUnknownToken() {
    assertThatThrownBy(() -> adapter.refresh(refresh.generateJwt("1", "ADMIN")))
        .isInstanceOf(TokenException.class);
  }

  @Test
  @DisplayName("만료된 관리자 갱신 토큰은 거부한다")
  void rejectsExpiredToken() {
    var token =
        Jwts.builder()
            .subject("1")
            .issuer("test-issuer")
            .claim("tokenType", "ADMIN")
            .expiration(Date.from(Instant.now().minusSeconds(60)))
            .signWith(key)
            .compact();
    repository.save(1L, token, Duration.ofMinutes(1));
    assertThatThrownBy(() -> adapter.refresh(token)).isInstanceOf(TokenException.class);
    assertThat(repository.tokens).contains("1:" + token);
  }

  @Test
  @DisplayName("숫자가 아닌 관리자 식별자를 가진 토큰은 거부한다")
  void rejectsInvalidId() {
    assertThatThrownBy(() -> adapter.refresh(refresh.generateJwt("invalid", "ADMIN")))
        .isInstanceOf(TokenException.class);
  }

  @Test
  @DisplayName("로그아웃 또는 비밀번호 변경으로 폐기된 토큰은 사용할 수 없다")
  void rejectsRevokedToken() {
    var token = adapter.issue(1L);
    repository.deleteAll(1L);
    assertThatThrownBy(() -> adapter.refresh(token.refreshToken()))
        .isInstanceOf(TokenException.class);
  }

  @Test
  @DisplayName("다른 키로 서명한 관리자 갱신 토큰은 거부한다")
  void rejectsDifferentSigningKey() {
    var otherKey =
        Keys.hmacShaKeyFor("abcdefghijklmnopqrstuvwxyz012345".getBytes(StandardCharsets.UTF_8));
    var token =
        Jwts.builder()
            .subject("1")
            .issuer("test-issuer")
            .claim("tokenType", "ADMIN")
            .expiration(Date.from(Instant.now().plusSeconds(3600)))
            .signWith(otherKey)
            .compact();
    repository.save(1L, token, Duration.ofMinutes(1));
    assertThatThrownBy(() -> adapter.refresh(token)).isInstanceOf(TokenException.class);
    assertThat(repository.tokens).contains("1:" + token);
  }

  private static class Tokens implements AdminRefreshTokenRepositoryPort {
    private final Set<String> tokens = new HashSet<>();

    public void save(Long id, String token, Duration ttl) {
      tokens.add(id + ":" + token);
    }

    public boolean delete(Long id, String token) {
      return tokens.remove(id + ":" + token);
    }

    public void deleteAll(Long id) {
      tokens.removeIf(token -> token.startsWith(id + ":"));
    }
  }
}
