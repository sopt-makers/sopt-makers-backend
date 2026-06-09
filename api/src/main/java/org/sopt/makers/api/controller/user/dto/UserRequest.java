package org.sopt.makers.api.controller.user.dto;

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.auth.facade.AuthFacade;
import org.sopt.makers.domain.user.Team;
import org.sopt.makers.domain.user.service.UserCommandService;

@NoArgsConstructor(access = PRIVATE)
public final class UserRequest {

  public record GetUserProfileByIds(List<Long> userIds) {}

  public record UserProfileInfo(
      String profileImage,
      LocalDate birthday,
      @NotNull(message = "핸드폰 번호는 필수 입력 값입니다.") String phone,
      @NotNull(message = "이메일은 필수 입력 값입니다.")
          @Pattern(
              regexp =
                  "^[0-9a-zA-Z]([-_\\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\\.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}$")
          String email,
      List<SoptActivityInfo> soptActivities) {

    public AuthFacade.UpdateProfileCommand toCommand(Long userId) {
      List<UserCommandService.ActivityUpdateCommand> activityUpdates =
          soptActivities == null
              ? List.of()
              : soptActivities.stream().map(SoptActivityInfo::toCommand).toList();
      return new AuthFacade.UpdateProfileCommand(
          email, phone, birthday, profileImage, activityUpdates);
    }
  }

  public record SoptActivityInfo(Long activityId, String team) {

    public UserCommandService.ActivityUpdateCommand toCommand() {
      Team teamEnum =
          team == null
              ? null
              : Arrays.stream(Team.values())
                  .filter(t -> t.getDisplayName().equals(team))
                  .findFirst()
                  .orElseThrow(() -> new IllegalArgumentException("Unknown team: " + team));
      return new UserCommandService.ActivityUpdateCommand(activityId, teamEnum);
    }
  }
}
