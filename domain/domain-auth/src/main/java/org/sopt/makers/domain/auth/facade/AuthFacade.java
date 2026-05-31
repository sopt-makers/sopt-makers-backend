package org.sopt.makers.domain.auth.facade;

import static org.sopt.makers.domain.auth.exception.AuthFailure.ALREADY_REGISTERED_SOCIAL_ACCOUNT;
import static org.sopt.makers.domain.auth.exception.AuthFailure.ALREADY_REGISTER_PHONE_NUMBER;
import static org.sopt.makers.domain.auth.exception.AuthFailure.NOT_FOUND_REGISTER_INFO;
import static org.sopt.makers.domain.auth.exception.AuthFailure.NOT_FOUND_USER_WITH_SOCIAL_ACCOUNT;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.core.type.OAuthPlatform;
import org.sopt.makers.domain.auth.PhoneVerification;
import org.sopt.makers.domain.auth.PhoneVerificationType;
import org.sopt.makers.domain.auth.exception.AuthException;
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
import org.sopt.makers.domain.user.port.UserRegisterInfoRepositoryPort;
import org.sopt.makers.domain.user.service.UserCommandService;
import org.sopt.makers.domain.user.service.UserCommandService.ActivityUpdateCommand;
import org.sopt.makers.domain.user.service.UserQueryService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthFacade {

  private static final String VERIFICATION_MESSAGE_FORMAT = "[SOPT makers]\n인증번호 [%s]를 입력해주세요.";

  // auth domain
  private final AuthService authService;
  private final OAuthAuthenticatorPort oAuthAuthenticatorPort;
  private final TokenIssuerPort tokenIssuerPort;
  private final SmsSenderPort smsSenderPort;

  // user domain (domain-auth → domain-user 허용)
  private final UserQueryService userQueryService;
  private final UserCommandService userCommandService;
  private final UserRegisterInfoRepositoryPort userRegisterInfoRepositoryPort;

  // ──────────────────────────────────────────────────
  // 로그인
  // ──────────────────────────────────────────────────

  @Transactional
  public TokenPair login(String idToken, OAuthPlatform platform) {
    String platformId = oAuthAuthenticatorPort.getIdentifier(idToken, platform);

    User user =
        userQueryService
            .findBySocialAccount(platform, platformId)
            .orElseThrow(() -> new AuthException(NOT_FOUND_USER_WITH_SOCIAL_ACCOUNT));

    if (user.isFirstLogin()) {
      userCommandService.completeFirstLogin(user.getId());
    }

    Role role = user.getActivities().getLastActivity().getRole();
    return tokenIssuerPort.issue(user.getId(), role);
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
    authService.validateVerified(phone, PhoneVerificationType.REGISTER);

    String platformId = oAuthAuthenticatorPort.getIdentifier(idToken, platform);
    SocialAccount socialAccount = SocialAccount.of(platformId, platform);

    UserRegisterInfo registerInfo =
        userRegisterInfoRepositoryPort
            .findByPhone(phone)
            .orElseThrow(() -> new AuthException(NOT_FOUND_REGISTER_INFO));

    Profile profile =
        Profile.of(
            registerInfo.getName(),
            registerInfo.getEmail(),
            registerInfo.getPhone(),
            registerInfo.getBirthday());

    Activity activity =
        Activity.of(registerInfo.getGeneration(), null, registerInfo.getPart(), true);

    try {
      User newUser = userCommandService.createUser(socialAccount, profile);
      userCommandService.addActivity(newUser.getId(), activity);
    } catch (DataIntegrityViolationException e) {
      log.error("중복된 소셜 계정으로 회원가입 시도 platformId={}", platformId);
      throw new AuthException(ALREADY_REGISTERED_SOCIAL_ACCOUNT);
    }

    userRegisterInfoRepositoryPort.delete(registerInfo);
  }

  // ──────────────────────────────────────────────────
  // 전화번호 인증
  // ──────────────────────────────────────────────────

  @Transactional
  public void createPhoneVerification(Long userId, String phone, PhoneVerificationType type) {
    String name = resolveVerificationName(userId, phone, type);
    PhoneVerification verification = authService.createVerification(name, phone, type);
    String message =
        String.format(VERIFICATION_MESSAGE_FORMAT, verification.verificationCode().code());
    smsSenderPort.send(phone, message);
  }

  @Transactional
  public void verifyPhoneCode(String phone, String code, PhoneVerificationType type) {
    authService.verifyCode(phone, code, type);
  }

  private String resolveVerificationName(Long userId, String phone, PhoneVerificationType type) {
    return switch (type) {
      case REGISTER ->
          userRegisterInfoRepositoryPort
              .findByPhone(phone)
              .map(UserRegisterInfo::getName)
              .orElseThrow(
                  () -> {
                    if (userQueryService.existsByPhone(phone)) {
                      throw new AuthException(ALREADY_REGISTER_PHONE_NUMBER);
                    }
                    return new AuthException(NOT_FOUND_REGISTER_INFO);
                  });
      case SEARCH_SOCIAL_PLATFORM, CHANGE_SOCIAL_PLATFORM ->
          userQueryService.getByPhone(phone).getProfile().name();
      case CHANGE_PHONE_NUMBER -> userQueryService.getById(userId).getProfile().name();
    };
  }

  // ──────────────────────────────────────────────────
  // 소셜 계정 변경
  // ──────────────────────────────────────────────────

  @Transactional
  public void updateSocialAccount(String phone, String idToken, OAuthPlatform platform) {
    authService.validateVerified(phone, PhoneVerificationType.CHANGE_SOCIAL_PLATFORM);

    User user = userQueryService.getByPhone(phone);
    String platformId = oAuthAuthenticatorPort.getIdentifier(idToken, platform);
    SocialAccount newAccount = SocialAccount.of(platformId, platform);

    userCommandService.updateSocialAccount(user.getId(), newAccount);
  }

  // ──────────────────────────────────────────────────
  // 소셜 플랫폼 조회
  // ──────────────────────────────────────────────────

  @Transactional(readOnly = true)
  public OAuthPlatform getSocialPlatform(String phone) {
    PhoneVerification verification =
        authService.findVerifiedOrThrow(phone, PhoneVerificationType.SEARCH_SOCIAL_PLATFORM);
    User user = userQueryService.getByPhone(verification.phone());
    return user.getSocialAccount().authPlatformType();
  }

  // ──────────────────────────────────────────────────
  // 프로필 수정 (전화번호 변경 시 인증 포함)
  // ──────────────────────────────────────────────────

  @Transactional
  public void updateProfile(
      Long userId, Profile newProfile, List<ActivityUpdateCommand> activityUpdates) {

    User current = userQueryService.getWithActivitiesById(userId);
    boolean isPhoneChanged = !current.getProfile().phone().equals(newProfile.phone());

    if (isPhoneChanged) {
      authService.validateVerified(newProfile.phone(), PhoneVerificationType.CHANGE_PHONE_NUMBER);
    }

    userCommandService.updateProfileWithActivities(userId, newProfile, activityUpdates);
  }
}
