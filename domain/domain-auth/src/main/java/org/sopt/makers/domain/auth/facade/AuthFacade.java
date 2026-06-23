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
import org.sopt.makers.domain.user.UserCareer;
import org.sopt.makers.domain.user.UserFavor;
import org.sopt.makers.domain.user.UserLink;
import org.sopt.makers.domain.user.UserRegisterInfo;
import org.sopt.makers.domain.user.WorkPreference;
import org.sopt.makers.domain.user.port.UserRegisterInfoRepositoryPort;
import org.sopt.makers.domain.user.service.UserCommandService;
import org.sopt.makers.domain.user.service.UserQueryService;
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

  // user domain (domain-auth → domain-user 허용)
  private final UserQueryService userQueryService;
  private final UserCommandService userCommandService;
  private final UserRegisterInfoRepositoryPort userRegisterInfoRepositoryPort;

  public record LoginResult(String accessToken, String refreshToken, boolean isFirstLogin) {}

  public record VerifyResult(String name, String phone) {}

  public record UpdateProfileCommand(
      String email,
      String phone,
      String profileImage,
      List<UserCommandService.ActivityUpdateCommand> activityUpdates,
      // 플레이그라운드 확장 프로필
      String address,
      String university,
      String major,
      String introduction,
      String skill,
      String mbti,
      String mbtiDescription,
      Double sojuCapacity,
      String interest,
      UserFavor userFavor,
      String idealType,
      String selfIntroduction,
      Boolean allowOfficial,
      Boolean isPhoneBlind,
      WorkPreference workPreference,
      List<UserLink> links,
      List<UserCareer> careers) {}

  // ──────────────────────────────────────────────────
  // 로그인
  // ──────────────────────────────────────────────────

  @Transactional
  public LoginResult login(String idToken, OAuthPlatform platform) {
    String platformId = oAuthAuthenticatorPort.getIdentifier(idToken, platform);

    User user =
        userQueryService
            .findBySocialAccount(platform, platformId)
            .orElseThrow(() -> new AuthException(NOT_FOUND_USER_WITH_SOCIAL_ACCOUNT));

    boolean isFirstLogin = user.isFirstLogin();
    if (isFirstLogin) {
      userCommandService.completeFirstLogin(user.id());
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
    authService.validateVerified(phone, PhoneVerificationType.REGISTER);

    String platformId = oAuthAuthenticatorPort.getIdentifier(idToken, platform);
    SocialAccount socialAccount = SocialAccount.of(platformId, platform);

    userQueryService
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
      User newUser = userCommandService.createUser(socialAccount, profile);
      userCommandService.addActivity(newUser.id(), activity);
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
    String name = resolveVerificationName(userId, phone, type);
    PhoneVerification verification = authService.createVerification(name, phone, type);
    String message =
        String.format(VERIFICATION_MESSAGE_FORMAT, verification.verificationCode().code());
    smsSenderPort.send(phone, message);
  }

  @Transactional
  public VerifyResult verifyPhoneCode(String phone, String code, PhoneVerificationType type) {
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
                    if (userQueryService.existsByPhone(phone)) {
                      throw new AuthException(ALREADY_REGISTER_PHONE_NUMBER);
                    }
                    return new AuthException(NOT_FOUND_REGISTER_INFO);
                  });
      case SEARCH_SOCIAL_PLATFORM, CHANGE_SOCIAL_PLATFORM ->
          userQueryService.getByPhone(phone).profile().name();
      case CHANGE_PHONE_NUMBER -> userQueryService.getById(userId).profile().name();
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

    userCommandService.updateSocialAccount(user.id(), newAccount);
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
    User user = userQueryService.getByPhone(verification.phone());
    return user.socialAccount().authPlatformType();
  }

  // ──────────────────────────────────────────────────
  // 프로필 수정 (전화번호 변경 시 인증 포함)
  // ──────────────────────────────────────────────────

  @Transactional
  public void updateProfile(Long userId, UpdateProfileCommand command) {
    User current = userQueryService.getWithActivitiesById(userId);
    Profile currentProfile = current.profile();

    if (!currentProfile.phone().equals(command.phone())) {
      authService.validateVerified(command.phone(), PhoneVerificationType.CHANGE_PHONE_NUMBER);
    }

    Profile updatedProfile =
        currentProfile.update(
            command.email(),
            command.phone(),
            command.profileImage(),
            command.address(),
            command.university(),
            command.major(),
            command.introduction(),
            command.skill(),
            command.mbti(),
            command.mbtiDescription(),
            command.sojuCapacity(),
            command.interest(),
            command.userFavor(),
            command.idealType(),
            command.selfIntroduction(),
            command.allowOfficial(),
            command.isPhoneBlind(),
            command.workPreference(),
            command.links(),
            command.careers());

    userCommandService.updateProfileWithActivities(
        userId, updatedProfile, command.activityUpdates());
  }
}
