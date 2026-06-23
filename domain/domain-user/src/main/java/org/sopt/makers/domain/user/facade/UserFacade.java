package org.sopt.makers.domain.user.facade;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.Profile;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.command.UpdateProfileCommand;
import org.sopt.makers.domain.user.port.PhoneVerificationValidatorPort;
import org.sopt.makers.domain.user.service.UserCommandService;
import org.sopt.makers.domain.user.service.UserQueryService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserFacade {

  private final UserQueryService userQueryService;
  private final UserCommandService userCommandService;
  private final PhoneVerificationValidatorPort phoneVerificationValidatorPort;

  @Transactional
  public void updateProfile(Long userId, UpdateProfileCommand command) {
    User current = userQueryService.getWithActivitiesById(userId);
    Profile currentProfile = current.profile();
    boolean isPhoneNumberChanged = !currentProfile.phone().equals(command.phone());

    if (isPhoneNumberChanged) {
      phoneVerificationValidatorPort.validateChangePhoneNumberVerified(command.phone());
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
