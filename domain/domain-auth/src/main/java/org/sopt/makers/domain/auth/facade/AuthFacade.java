package org.sopt.makers.domain.auth.facade;

import static org.sopt.makers.domain.auth.exception.AuthFailure.ALREADY_REGISTERED_SOCIAL_ACCOUNT;
import static org.sopt.makers.domain.auth.exception.AuthFailure.ALREADY_REGISTER_PHONE_NUMBER;
import static org.sopt.makers.domain.auth.exception.AuthFailure.INVALID_PHONE_VERIFICATION_CODE;
import static org.sopt.makers.domain.auth.exception.AuthFailure.NOT_FOUND_REGISTER_INFO;
import static org.sopt.makers.domain.auth.exception.AuthFailure.NOT_FOUND_USER_WITH_SOCIAL_ACCOUNT;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.core.type.OAuthPlatform;
import org.sopt.makers.domain.auth.PhoneVerification;
import org.sopt.makers.domain.auth.PhoneVerificationType;
import org.sopt.makers.domain.auth.exception.AuthException;
import org.sopt.makers.domain.auth.port.BypassLoginPort;
import org.sopt.makers.domain.auth.port.OAuthAuthenticatorPort;
import org.sopt.makers.domain.auth.port.SmsSenderPort;
import org.sopt.makers.domain.auth.port.TokenIssuerPort;
import org.sopt.makers.domain.auth.port.TokenIssuerPort.TokenPair;
import org.sopt.makers.domain.auth.service.AuthService;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.Profile;
import org.sopt.makers.domain.user.Role;
import org.sopt.makers.domain.user.SocialAccount;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.UserRegisterInfo;
import org.sopt.makers.domain.user.port.AuthUserPort;
import org.sopt.makers.domain.user.port.UserRegisterInfoRepositoryPort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthFacade {

  private static final String VERIFICATION_MESSAGE_FORMAT = "[SOPT makers]\n인증번호 [%s]를 입력해주세요.";
  private static final String SOCIAL_ACCOUNT_UNIQUE_CONSTRAINT =
      "UK_AUTH_PLATFORM_ID_AND_AUTH_PLATFORM_TYPE";

  // auth domain
  private final AuthService authService;
  private final OAuthAuthenticatorPort oAuthAuthenticatorPort;
  private final TokenIssuerPort tokenIssuerPort;
  private final SmsSenderPort smsSenderPort;
  private final BypassLoginPort bypassLoginPort;

  private final AuthUserPort authUserPort;
  private final UserRegisterInfoRepositoryPort userRegisterInfoRepositoryPort;

  public record LoginResult(String accessToken, String refreshToken, boolean isFirstLogin) {}

  public record VerifyResult(String name, String phone) {}

  // ──────────────────────────────────────────────────
  // 로그인
  // ──────────────────────────────────────────────────

  @Transactional
  public LoginResult login(String idToken, OAuthPlatform platform) {
    String platformId = oAuthAuthenticatorPort.getIdentifier(idToken, platform);

    User user =
        authUserPort
            .findBySocialAccount(platform, platformId)
            .orElseThrow(() -> new AuthException(NOT_FOUND_USER_WITH_SOCIAL_ACCOUNT));

    boolean isFirstLogin = user.isFirstLogin();
    if (isFirstLogin) {
      authUserPort.completeFirstLogin(user.id());
    }

    Role role = user.activities().getLastActivity().role();
    TokenPair tokenPair = tokenIssuerPort.issue(user.id(), role);
    return new LoginResult(tokenPair.accessToken(), tokenPair.refreshToken(), isFirstLogin);
  }

  @Transactional(readOnly = true)
  public TokenPair refresh(String expiredAccessToken, String refreshToken) {
    return tokenIssuerPort.refresh(expiredAccessToken, refreshToken);
  }

  // ──────────────────────────────────────────────────
  // 회원가입
  // ──────────────────────────────────────────────────

  @Transactional
  public void signUp(String idToken, String phone, OAuthPlatform platform) {
    if (isBypassLoginPhone(phone)) {
      signUpForBypassLogin(idToken, platform);
      return;
    }

    authService.validateVerified(phone, PhoneVerificationType.REGISTER);

    String platformId = oAuthAuthenticatorPort.getIdentifier(idToken, platform);
    SocialAccount socialAccount = SocialAccount.of(platformId, platform);

    authUserPort
        .findBySocialAccount(platform, platformId)
        .ifPresent(
            u -> {
              throw new AuthException(ALREADY_REGISTERED_SOCIAL_ACCOUNT);
            });

    UserRegisterInfo registerInfo =
        userRegisterInfoRepositoryPort
            .findByPhone(phone)
            .orElseThrow(() -> new AuthException(NOT_FOUND_REGISTER_INFO));

    Profile profile =
        Profile.of(
            registerInfo.name(),
            registerInfo.email(),
            registerInfo.phone(),
            registerInfo.birthday());

    Activity activity = Activity.of(registerInfo.generation(), null, registerInfo.part(), true);

    try {
      User newUser = authUserPort.createUser(socialAccount, profile);
      authUserPort.addActivity(newUser.id(), activity);
    } catch (DataIntegrityViolationException e) {
      if (!isSocialAccountConstraintViolation(e)) {
        throw e;
      }
      log.error("동시성으로 인한 중복 소셜 계정 가입 시도 platformId={}", platformId);
      throw new AuthException(ALREADY_REGISTERED_SOCIAL_ACCOUNT);
    }

    userRegisterInfoRepositoryPort.delete(registerInfo);
  }

  // ──────────────────────────────────────────────────
  // 전화번호 인증
  // ──────────────────────────────────────────────────

  @Transactional
  public void createPhoneVerification(Long userId, String phone, PhoneVerificationType type) {
    if (isBypassLoginPhone(phone)) {
      return;
    }
    String name = resolveVerificationName(userId, phone, type);
    PhoneVerification verification = authService.createVerification(name, phone, type);
    String message =
        String.format(VERIFICATION_MESSAGE_FORMAT, verification.verificationCode().code());
    smsSenderPort.send(phone, message);
  }

  @Transactional
  public VerifyResult verifyPhoneCode(String phone, String code, PhoneVerificationType type) {
    if (isBypassLoginVerification(phone, code)) {
      return new VerifyResult(bypassLoginPort.name(), bypassLoginPort.phone());
    }
    PhoneVerification verified = authService.verifyCode(phone, code, type);
    return new VerifyResult(verified.name(), verified.phone());
  }

  private String resolveVerificationName(Long userId, String phone, PhoneVerificationType type) {
    return switch (type) {
      case REGISTER ->
          userRegisterInfoRepositoryPort
              .findByPhone(phone)
              .map(UserRegisterInfo::name)
              .orElseThrow(
                  () -> {
                    if (authUserPort.existsByPhone(phone)) {
                      throw new AuthException(ALREADY_REGISTER_PHONE_NUMBER);
                    }
                    return new AuthException(NOT_FOUND_REGISTER_INFO);
                  });
      case SEARCH_SOCIAL_PLATFORM, CHANGE_SOCIAL_PLATFORM ->
          authUserPort.getByPhone(phone).profile().name();
      case CHANGE_PHONE_NUMBER -> authUserPort.getById(userId).profile().name();
    };
  }

  // ──────────────────────────────────────────────────
  // 소셜 계정 변경
  // ──────────────────────────────────────────────────

  @Transactional
  public void updateSocialAccount(String phone, String idToken, OAuthPlatform platform) {
    authService.validateVerified(phone, PhoneVerificationType.CHANGE_SOCIAL_PLATFORM);

    User user = authUserPort.getByPhone(phone);
    String platformId = oAuthAuthenticatorPort.getIdentifier(idToken, platform);
    SocialAccount newAccount = SocialAccount.of(platformId, platform);

    authUserPort.updateSocialAccount(user.id(), newAccount);
  }

  private boolean isBypassLoginPhone(String phone) {
    boolean matchesBypassLoginPhone = bypassLoginPort.phone().equals(phone);
    return matchesBypassLoginPhone;
  }

  private boolean isBypassLoginVerification(String phone, String code) {
    boolean matchesBypassLoginPhone = bypassLoginPort.phone().equals(phone);
    if (!matchesBypassLoginPhone) {
      return false;
    }
    boolean matchesBypassLoginCode = bypassLoginPort.code().equals(code);
    if (!matchesBypassLoginCode) {
      throw new AuthException(INVALID_PHONE_VERIFICATION_CODE);
    }
    return true;
  }

  private void signUpForBypassLogin(String idToken, OAuthPlatform platform) {
    User bypassLoginUser = authUserPort.getByPhone(bypassLoginPort.phone());
    String platformId = oAuthAuthenticatorPort.getIdentifier(idToken, platform);
    SocialAccount newAccount = SocialAccount.of(platformId, platform);
    authUserPort.updateSocialAccount(bypassLoginUser.id(), newAccount);
  }

  private boolean isSocialAccountConstraintViolation(DataIntegrityViolationException e) {
    for (Throwable t = e; t != null; t = t.getCause()) {
      if (t.getMessage() != null && t.getMessage().contains(SOCIAL_ACCOUNT_UNIQUE_CONSTRAINT)) {
        return true;
      }
    }
    return false;
  }

  // ──────────────────────────────────────────────────
  // 소셜 플랫폼 조회
  // ──────────────────────────────────────────────────

  @Transactional(readOnly = true)
  public OAuthPlatform getSocialPlatform(String phone) {
    PhoneVerification verification =
        authService.findVerifiedOrThrow(phone, PhoneVerificationType.SEARCH_SOCIAL_PLATFORM);
    User user = authUserPort.getByPhone(verification.phone());
    return user.socialAccount().authPlatformType();
  }
}
