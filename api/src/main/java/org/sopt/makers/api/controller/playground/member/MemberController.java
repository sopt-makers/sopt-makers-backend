package org.sopt.makers.api.controller.playground.member;

import static org.sopt.makers.api.controller.playground.member.MemberSuccessCode.CHECK_ACTIVITY;
import static org.sopt.makers.api.controller.playground.member.MemberSuccessCode.CREATE_PROFILE;
import static org.sopt.makers.api.controller.playground.member.MemberSuccessCode.DELETE_PROFILE_LINK;
import static org.sopt.makers.api.controller.playground.member.MemberSuccessCode.GET_MEMBER;
import static org.sopt.makers.api.controller.playground.member.MemberSuccessCode.GET_MY_INFO;
import static org.sopt.makers.api.controller.playground.member.MemberSuccessCode.GET_PROFILE;
import static org.sopt.makers.api.controller.playground.member.MemberSuccessCode.GET_WORK_PREFERENCE;
import static org.sopt.makers.api.controller.playground.member.MemberSuccessCode.SEARCH_MEMBER;
import static org.sopt.makers.api.controller.playground.member.MemberSuccessCode.UPDATE_PROFILE;
import static org.sopt.makers.api.controller.playground.member.MemberSuccessCode.UPDATE_WORK_PREFERENCE;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.playground.member.dto.CheckActivityRequest;
import org.sopt.makers.api.controller.playground.member.dto.MemberInfoResponse;
import org.sopt.makers.api.controller.playground.member.dto.MemberProfileResponse;
import org.sopt.makers.api.controller.playground.member.dto.MemberProfileSaveRequest;
import org.sopt.makers.api.controller.playground.member.dto.MemberProfileSpecificResponse;
import org.sopt.makers.api.controller.playground.member.dto.MemberProfileUpdateRequest;
import org.sopt.makers.api.controller.playground.member.dto.MemberResponse;
import org.sopt.makers.api.controller.playground.member.dto.WorkPreferenceResponse;
import org.sopt.makers.api.controller.playground.member.dto.WorkPreferenceUpdateRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.playground.member.profile.service.UserProfileCommandService;
import org.sopt.makers.domain.playground.member.profile.service.UserProfileQueryService;
import org.sopt.makers.domain.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController implements MemberApi {

  private final UserProfileQueryService userProfileQueryService;
  private final UserProfileCommandService userProfileCommandService;

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<BaseResponse<?>> getMember(@PathVariable Long id) {
    return ResponseFactory.success(GET_MEMBER, MemberResponse.from(userProfileQueryService.getMemberSummary(id)));
  }

  @Override
  @GetMapping("/me")
  public ResponseEntity<BaseResponse<?>> getMyInfo(@CurrentUserId Long userId) {
    return ResponseFactory.success(GET_MY_INFO, MemberInfoResponse.from(userProfileQueryService.getMyInfo(userId)));
  }

  @Override
  @GetMapping("/search")
  public ResponseEntity<BaseResponse<?>> searchMember(@RequestParam String name) {
    List<MemberResponse> responses =
        userProfileQueryService.searchByName(name).stream().map(MemberResponse::from).toList();
    return ResponseFactory.success(SEARCH_MEMBER, responses);
  }

  @Override
  @PostMapping("/profile")
  public ResponseEntity<BaseResponse<?>> createProfile(
      @CurrentUserId Long userId, @Valid @RequestBody MemberProfileSaveRequest request) {
    User member =
        userProfileCommandService.saveProfile(
            userId,
            request.email(),
            request.phone(),
            request.profileImage(),
            toActivityInputs(request.activities()),
            request.address(),
            request.university(),
            request.major(),
            request.introduction(),
            request.skill(),
            request.mbti(),
            request.mbtiDescription(),
            request.sojuCapacity(),
            request.interest(),
            toUserFavorInput(request.userFavor()),
            request.idealType(),
            request.selfIntroduction(),
            toLinkInputs(request.links()),
            toCareerInputs(request.careers()),
            request.allowOfficial(),
            request.isPhoneBlind());
    boolean isCoffeeChatActivate = userProfileQueryService.isCoffeeChatActive(userId);
    return ResponseFactory.success(CREATE_PROFILE, MemberProfileResponse.from(member, isCoffeeChatActivate));
  }

  @Override
  @PutMapping("/profile")
  public ResponseEntity<BaseResponse<?>> updateProfile(
      @CurrentUserId Long userId, @Valid @RequestBody MemberProfileUpdateRequest request) {
    User member =
        userProfileCommandService.updateProfile(
            userId,
            request.email(),
            request.phone(),
            request.profileImage(),
            toActivityInputsFromUpdate(request.activities()),
            request.address(),
            request.university(),
            request.major(),
            request.introduction(),
            request.skill(),
            request.mbti(),
            request.mbtiDescription(),
            request.sojuCapacity(),
            request.interest(),
            toUserFavorInput(request.userFavor()),
            request.idealType(),
            request.selfIntroduction(),
            toWorkPreferenceInput(request.workPreference()),
            toLinkInputsFromUpdate(request.links()),
            toCareerInputsFromUpdate(request.careers()),
            request.allowOfficial(),
            request.isPhoneBlind());
    boolean isCoffeeChatActivate = userProfileQueryService.isCoffeeChatActive(userId);
    return ResponseFactory.success(UPDATE_PROFILE, MemberProfileResponse.from(member, isCoffeeChatActivate));
  }

  @Override
  @PatchMapping("/work-preference")
  public ResponseEntity<BaseResponse<?>> updateWorkPreference(
      @CurrentUserId Long userId, @RequestBody WorkPreferenceUpdateRequest request) {
    userProfileCommandService.updateWorkPreference(userId, toWorkPreferenceInput(request));
    return ResponseFactory.success(UPDATE_WORK_PREFERENCE);
  }

  @Override
  @GetMapping("/work-preference")
  public ResponseEntity<BaseResponse<?>> getWorkPreference(@CurrentUserId Long userId) {
    User user = userProfileQueryService.getMemberUser(userId);
    return ResponseFactory.success(GET_WORK_PREFERENCE, WorkPreferenceResponse.from(user.profile().workPreference()));
  }

  @Override
  @GetMapping("/profile/{id}")
  public ResponseEntity<BaseResponse<?>> getProfile(@PathVariable Long id, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_PROFILE, MemberProfileSpecificResponse.from(userProfileQueryService.getProfileDetail(id, userId)));
  }

  @Override
  @GetMapping("/profile/me")
  public ResponseEntity<BaseResponse<?>> getMyProfile(@CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_PROFILE, MemberProfileSpecificResponse.from(userProfileQueryService.getProfileDetail(userId, userId)));
  }

  @Override
  @PutMapping("/activity/check")
  public ResponseEntity<BaseResponse<?>> checkActivity(
      @CurrentUserId Long userId, @Valid @RequestBody CheckActivityRequest request) {
    userProfileCommandService.checkActivity(userId, request.isCheck());
    return ResponseFactory.success(CHECK_ACTIVITY, Map.of("유저 기수 확인 여부가 변경됐습니다.", true));
  }

  @Override
  @DeleteMapping("/profile/link/{linkId}")
  public ResponseEntity<BaseResponse<?>> deleteProfileLink(
      @PathVariable Long linkId, @CurrentUserId Long userId) {
    userProfileCommandService.deleteLink(userId, linkId);
    return ResponseFactory.success(DELETE_PROFILE_LINK);
  }

  private List<UserProfileCommandService.ActivityInput> toActivityInputs(
      List<MemberProfileSaveRequest.MemberSoptActivitySaveRequest> activities) {
    if (activities == null) {
      return List.of();
    }
    return activities.stream()
        .map(a -> new UserProfileCommandService.ActivityInput(a.generation(), a.team()))
        .toList();
  }

  private List<UserProfileCommandService.ActivityInput> toActivityInputsFromUpdate(
      List<MemberProfileUpdateRequest.MemberSoptActivityUpdateRequest> activities) {
    if (activities == null) {
      return List.of();
    }
    return activities.stream()
        .map(a -> new UserProfileCommandService.ActivityInput(a.generation(), a.team()))
        .toList();
  }

  private UserProfileCommandService.UserFavorInput toUserFavorInput(
      MemberProfileSaveRequest.UserFavorRequest request) {
    if (request == null) {
      return null;
    }
    return new UserProfileCommandService.UserFavorInput(
        request.isPourSauceLover(),
        request.isHardPeachLover(),
        request.isMintChocoLover(),
        request.isRedBeanFishBreadLover(),
        request.isSojuLover(),
        request.isRiceTteokLover());
  }

  private UserProfileCommandService.UserFavorInput toUserFavorInput(
      MemberProfileUpdateRequest.UserFavorRequest request) {
    if (request == null) {
      return null;
    }
    return new UserProfileCommandService.UserFavorInput(
        request.isPourSauceLover(),
        request.isHardPeachLover(),
        request.isMintChocoLover(),
        request.isRedBeanFishBreadLover(),
        request.isSojuLover(),
        request.isRiceTteokLover());
  }

  private UserProfileCommandService.WorkPreferenceInput toWorkPreferenceInput(
      MemberProfileUpdateRequest.WorkPreferenceRequest request) {
    if (request == null) {
      return null;
    }
    return new UserProfileCommandService.WorkPreferenceInput(
        request.ideationStyle(), request.workTime(), request.communicationStyle(), request.workPlace(),
        request.feedbackStyle());
  }

  private UserProfileCommandService.WorkPreferenceInput toWorkPreferenceInput(WorkPreferenceUpdateRequest request) {
    return new UserProfileCommandService.WorkPreferenceInput(
        request.ideationStyle(), request.workTime(), request.communicationStyle(), request.workPlace(),
        request.feedbackStyle());
  }

  private List<UserProfileCommandService.LinkInput> toLinkInputs(
      List<MemberProfileSaveRequest.MemberLinkSaveRequest> links) {
    if (links == null) {
      return List.of();
    }
    return links.stream().map(l -> new UserProfileCommandService.LinkInput(null, l.title(), l.url())).toList();
  }

  private List<UserProfileCommandService.LinkInput> toLinkInputsFromUpdate(
      List<MemberProfileUpdateRequest.MemberLinkUpdateRequest> links) {
    if (links == null) {
      return List.of();
    }
    return links.stream().map(l -> new UserProfileCommandService.LinkInput(l.id(), l.title(), l.url())).toList();
  }

  private List<UserProfileCommandService.CareerInput> toCareerInputs(
      List<MemberProfileSaveRequest.MemberCareerSaveRequest> careers) {
    if (careers == null) {
      return List.of();
    }
    return careers.stream()
        .map(
            c ->
                new UserProfileCommandService.CareerInput(
                    c.companyName(), c.title(), c.startDate(), c.endDate(), c.isCurrent()))
        .toList();
  }

  private List<UserProfileCommandService.CareerInput> toCareerInputsFromUpdate(
      List<MemberProfileUpdateRequest.MemberCareerUpdateRequest> careers) {
    if (careers == null) {
      return List.of();
    }
    return careers.stream()
        .map(
            c ->
                new UserProfileCommandService.CareerInput(
                    c.companyName(), c.title(), c.startDate(), c.endDate(), c.isCurrent()))
        .toList();
  }
}
