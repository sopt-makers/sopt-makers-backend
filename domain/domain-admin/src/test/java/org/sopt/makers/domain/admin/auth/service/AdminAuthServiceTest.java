package org.sopt.makers.domain.admin.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.admin.auth.*;
import org.sopt.makers.domain.admin.auth.exception.AdminAuthException;
import org.sopt.makers.domain.admin.auth.port.*;
import org.sopt.makers.domain.admin.auth.port.AdminTokenIssuerPort.AdminTokenPair;

class AdminAuthServiceTest {
  private final AdminAccountRepositoryPort accounts = mock(AdminAccountRepositoryPort.class);
  private final AdminRefreshTokenRepositoryPort refreshTokens =
      mock(AdminRefreshTokenRepositoryPort.class);
  private final AdminTokenIssuerPort tokens = mock(AdminTokenIssuerPort.class);
  private final PasswordHashPort passwords = mock(PasswordHashPort.class);
  private final AdminAuthService service =
      new AdminAuthService(accounts, refreshTokens, tokens, passwords);
  private final AdminAccount account =
      new AdminAccount(1L, "admin@sopt.org", "hash", "관리자", AdminAccountType.SOPT);

  @Test
  @DisplayName("비밀번호를 검증한 관리자에게 토큰을 발급한다")
  void logsIn() {
    when(accounts.findByEmail(account.email())).thenReturn(Optional.of(account));
    when(passwords.matches("password", "hash")).thenReturn(true);
    var pair = new AdminTokenPair("access", "refresh");
    when(tokens.issue(1L)).thenReturn(pair);
    var result = service.login(account.email(), "password");
    assertThat(result.adminAccount()).isEqualTo(account);
    assertThat(result.tokenPair()).isEqualTo(pair);
  }

  @Test
  @DisplayName("잘못된 로그인 비밀번호에는 토큰을 발급하지 않는다")
  void rejectsPassword() {
    when(accounts.findByEmail(account.email())).thenReturn(Optional.of(account));
    assertThatThrownBy(() -> service.login(account.email(), "wrong"))
        .isInstanceOf(AdminAuthException.class);
    verifyNoInteractions(tokens);
  }

  @Test
  @DisplayName("갱신 토큰만으로 재발급을 요청한다")
  void refreshes() {
    var pair = new AdminTokenPair("access", "refresh");
    when(tokens.refresh("old-refresh")).thenReturn(pair);
    assertThat(service.refresh("old-refresh")).isEqualTo(pair);
    verify(tokens).refresh("old-refresh");
  }

  @Test
  @DisplayName("비밀번호를 변경하면 새 해시를 저장하고 모든 갱신 토큰을 폐기한다")
  void changesPassword() {
    when(accounts.findById(1L)).thenReturn(Optional.of(account));
    when(passwords.matches("old", "hash")).thenReturn(true);
    when(passwords.encode("new")).thenReturn("new-hash");
    service.changePassword(1L, "old", "new");
    verify(accounts)
        .save(
            new AdminAccount(
                1L, account.email(), "new-hash", account.name(), account.accountType()));
    verify(refreshTokens).deleteAll(1L);
  }

  @Test
  @DisplayName("기존 비밀번호가 틀리면 저장과 토큰 폐기를 수행하지 않는다")
  void rejectsPasswordChange() {
    when(accounts.findById(1L)).thenReturn(Optional.of(account));
    assertThatThrownBy(() -> service.changePassword(1L, "wrong", "new"))
        .isInstanceOf(AdminAuthException.class);
    verify(accounts, never()).save(any());
    verifyNoInteractions(refreshTokens);
  }
}
