package org.sopt.makers.api.controller.user.dto;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.Arrays;
import java.util.List;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.auth.facade.AuthFacade;
import org.sopt.makers.domain.user.Team;
import org.sopt.makers.domain.user.UserCareer;
import org.sopt.makers.domain.user.UserFavor;
import org.sopt.makers.domain.user.UserLink;
import org.sopt.makers.domain.user.WorkPreference;
import org.sopt.makers.domain.user.enums.CommunicationStyle;
import org.sopt.makers.domain.user.enums.FeedbackStyle;
import org.sopt.makers.domain.user.enums.IdeationStyle;
import org.sopt.makers.domain.user.enums.WorkPlace;
import org.sopt.makers.domain.user.enums.WorkTime;
import org.sopt.makers.domain.user.service.UserCommandService;

@NoArgsConstructor(access = PRIVATE)
public final class UserRequest {

  public record GetUserProfileByIds(List<Long> userIds) {}

  public record UserProfileInfo(
      String profileImage,
      @NotNull(message = "핸드폰 번호는 필수 입력 값입니다.")
          @Pattern(regexp = "^\\d{11}$", message = "잘못된 전화번호 형식입니다. '-'을 제외한 11자리 번호를 입력해주세요.")
          String phone,
      @NotNull(message = "이메일은 필수 입력 값입니다.")
          @Pattern(
              regexp =
                  "^[0-9a-zA-Z]([-_\\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\\.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}$")
          String email,
      List<SoptActivityInfo> soptActivities,
      String address,
      String university,
      String major,
      String introduction,
      String skill,
      String mbti,
      String mbtiDescription,
      Double sojuCapacity,
      String interest,
      UserFavorInfo userFavor,
      String idealType,
      String selfIntroduction,
      WorkPreferenceInfo workPreference,
      List<UserLinkInfo> links,
      List<UserCareerInfo> careers,
      Boolean allowOfficial,
      Boolean isPhoneBlind) {

    public AuthFacade.UpdateProfileCommand toCommand() {
      List<UserCommandService.ActivityUpdateCommand> activityUpdates =
          soptActivities == null
              ? List.of()
              : soptActivities.stream().map(SoptActivityInfo::toCommand).toList();

      UserFavor favor = userFavor != null ? userFavor.toDomain() : null;
      WorkPreference preference = workPreference != null ? workPreference.toDomain() : null;
      List<UserLink> linkList =
          links != null ? links.stream().map(UserLinkInfo::toDomain).toList() : List.of();
      List<UserCareer> careerList =
          careers != null ? careers.stream().map(UserCareerInfo::toDomain).toList() : List.of();

      return new AuthFacade.UpdateProfileCommand(
          email,
          phone,
          profileImage,
          activityUpdates,
          address,
          university,
          major,
          introduction,
          skill,
          mbti,
          mbtiDescription,
          sojuCapacity,
          interest,
          favor,
          idealType,
          selfIntroduction,
          allowOfficial,
          isPhoneBlind,
          preference,
          linkList,
          careerList);
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

  public record UserFavorInfo(
      Boolean isPourSauceLover,
      Boolean isHardPeachLover,
      Boolean isMintChocoLover,
      Boolean isRedBeanFishBreadLover,
      Boolean isSojuLover,
      Boolean isRiceTteokLover) {

    public UserFavor toDomain() {
      return UserFavor.of(
          isPourSauceLover,
          isHardPeachLover,
          isMintChocoLover,
          isRedBeanFishBreadLover,
          isSojuLover,
          isRiceTteokLover);
    }
  }

  public record WorkPreferenceInfo(
      String ideationStyle,
      String workTime,
      String communicationStyle,
      String workPlace,
      String feedbackStyle) {

    public WorkPreference toDomain() {
      return WorkPreference.of(
          IdeationStyle.fromValue(ideationStyle),
          WorkTime.fromValue(workTime),
          CommunicationStyle.fromValue(communicationStyle),
          WorkPlace.fromValue(workPlace),
          FeedbackStyle.fromValue(feedbackStyle));
    }
  }

  public record UserLinkInfo(String title, String url) {

    public UserLink toDomain() {
      return UserLink.of(null, null, title, url);
    }
  }

  public record UserCareerInfo(
      String companyName,
      String title,
      @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM") String startDate,
      @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM") String endDate,
      Boolean isCurrent) {

    public UserCareer toDomain() {
      return UserCareer.of(null, null, companyName, title, startDate, endDate, isCurrent);
    }
  }
}
