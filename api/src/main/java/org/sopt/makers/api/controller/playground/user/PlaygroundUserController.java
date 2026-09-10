package org.sopt.makers.api.controller.playground.user;

import static org.sopt.makers.api.controller.playground.user.PlaygroundUserSuccessCode.ACTIVATE_BLOCK;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserSuccessCode.CHECK_ACTIVITY;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserSuccessCode.CREATE_PROFILE;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserSuccessCode.DELETE_PROFILE_LINK;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserSuccessCode.GET_ASK_MEMBERS;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserSuccessCode.GET_BLOCK_STATUS;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserSuccessCode.GET_MEMBER;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserSuccessCode.GET_MEMBER_CREW;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserSuccessCode.GET_MEMBER_PROPERTY;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserSuccessCode.GET_MY_INFO;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserSuccessCode.GET_PROFILE;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserSuccessCode.GET_PROFILE_LIST;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserSuccessCode.GET_RECOMMENDATIONS;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserSuccessCode.GET_SAME_GENERATION_AND_PART_RECOMMENDATIONS;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserSuccessCode.GET_TL_MEMBERS;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserSuccessCode.GET_WORK_PREFERENCE;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserSuccessCode.GET_WORK_PREFERENCE_RECOMMENDATIONS;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserSuccessCode.REPORT_MEMBER;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserSuccessCode.SEARCH_MEMBER;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserSuccessCode.UPDATE_PROFILE;
import static org.sopt.makers.api.controller.playground.user.PlaygroundUserSuccessCode.UPDATE_WORK_PREFERENCE;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.playground.user.dto.AskUserResponse;
import org.sopt.makers.api.controller.playground.user.dto.CheckActivityRequest;
import org.sopt.makers.api.controller.playground.user.dto.MemberCrewResponse;
import org.sopt.makers.api.controller.playground.user.dto.SameGenerationAndPartRecommendResponse;
import org.sopt.makers.api.controller.playground.user.dto.TlMemberResponse;
import org.sopt.makers.api.controller.playground.user.dto.UserAllProfileResponse;
import org.sopt.makers.api.controller.playground.user.dto.UserBlockRequest;
import org.sopt.makers.api.controller.playground.user.dto.UserBlockResponse;
import org.sopt.makers.api.controller.playground.user.dto.UserInfoResponse;
import org.sopt.makers.api.controller.playground.user.dto.UserProfileResponse;
import org.sopt.makers.api.controller.playground.user.dto.UserProfileSaveRequest;
import org.sopt.makers.api.controller.playground.user.dto.UserProfileSpecificResponse;
import org.sopt.makers.api.controller.playground.user.dto.UserProfileUpdateRequest;
import org.sopt.makers.api.controller.playground.user.dto.UserPropertiesResponse;
import org.sopt.makers.api.controller.playground.user.dto.UserRecommendResponse;
import org.sopt.makers.api.controller.playground.user.dto.UserReportRequest;
import org.sopt.makers.api.controller.playground.user.dto.UserResponse;
import org.sopt.makers.api.controller.playground.user.dto.WorkPreferenceRecommendationResponse;
import org.sopt.makers.api.controller.playground.user.dto.WorkPreferenceResponse;
import org.sopt.makers.api.controller.playground.user.dto.WorkPreferenceUpdateRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.playground.member.ask.service.UserAskQueryService;
import org.sopt.makers.domain.playground.member.profile.UserSummary;
import org.sopt.makers.domain.playground.member.profile.service.AppJamTlService;
import org.sopt.makers.domain.playground.member.profile.service.UserProfileCommandService;
import org.sopt.makers.domain.playground.member.profile.service.UserProfileListService;
import org.sopt.makers.domain.playground.member.profile.service.UserProfileQueryService;
import org.sopt.makers.domain.playground.member.profile.service.UserPropertyService;
import org.sopt.makers.domain.playground.member.profile.service.UserRecommendationService;
import org.sopt.makers.domain.playground.member.relation.UserBlock;
import org.sopt.makers.domain.playground.member.relation.service.UserRelationService;
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
public class PlaygroundUserController implements PlaygroundUserApi {

  private static final int DEFAULT_CREW_PAGE_NO = 1;
  private static final int DEFAULT_CREW_TAKE = 10;

  private final UserProfileQueryService userProfileQueryService;
  private final UserProfileCommandService userProfileCommandService;
  private final UserProfileListService userProfileListService;
  private final UserRecommendationService userRecommendationService;
  private final AppJamTlService appJamTlService;
  private final UserAskQueryService userAskQueryService;
  private final UserRelationService userRelationService;
  private final UserPropertyService memberPropertyService;

  @Override
  @GetMapping("/{id}")
  public ResponseEntity<BaseResponse<?>> getMember(@PathVariable Long id) {
    return ResponseFactory.success(
        GET_MEMBER, UserResponse.from(userProfileQueryService.getMemberSummary(id)));
  }

  @Override
  @GetMapping("/me")
  public ResponseEntity<BaseResponse<?>> getMyInfo(@CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_MY_INFO, UserInfoResponse.from(userProfileQueryService.getMyInfo(userId)));
  }

  @Override
  @GetMapping("/search")
  public ResponseEntity<BaseResponse<?>> searchMember(@RequestParam String name) {
    List<UserResponse> responses =
        userProfileQueryService.searchByName(name).stream().map(UserResponse::from).toList();
    return ResponseFactory.success(SEARCH_MEMBER, responses);
  }

  @Override
  @PostMapping("/profile")
  public ResponseEntity<BaseResponse<?>> createProfile(
      @CurrentUserId Long userId, @Valid @RequestBody UserProfileSaveRequest request) {
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
    return ResponseFactory.success(
        CREATE_PROFILE, UserProfileResponse.from(member, isCoffeeChatActivate));
  }

  @Override
  @PutMapping("/profile")
  public ResponseEntity<BaseResponse<?>> updateProfile(
      @CurrentUserId Long userId, @Valid @RequestBody UserProfileUpdateRequest request) {
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
    return ResponseFactory.success(
        UPDATE_PROFILE, UserProfileResponse.from(member, isCoffeeChatActivate));
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
    return ResponseFactory.success(
        GET_WORK_PREFERENCE, WorkPreferenceResponse.from(user.profile().workPreference()));
  }

  @Override
  @GetMapping("/profile/{id}")
  public ResponseEntity<BaseResponse<?>> getProfile(
      @PathVariable Long id, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_PROFILE,
        UserProfileSpecificResponse.from(userProfileQueryService.getProfileDetail(id, userId)));
  }

  @Override
  @GetMapping("/profile/me")
  public ResponseEntity<BaseResponse<?>> getMyProfile(@CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_PROFILE,
        UserProfileSpecificResponse.from(userProfileQueryService.getProfileDetail(userId, userId)));
  }

  @Override
  @GetMapping("/profile")
  public ResponseEntity<BaseResponse<?>> getProfiles(
      @RequestParam(required = false) Integer filter,
      @RequestParam(required = false) Integer limit,
      @RequestParam(required = false) Integer offset,
      @RequestParam(required = false) String search,
      @RequestParam(required = false) Integer generation,
      @RequestParam(required = false) Integer employed,
      @RequestParam(required = false) Integer orderBy,
      @RequestParam(required = false) String mbti,
      @RequestParam(required = false) String team) {
    return ResponseFactory.success(
        GET_PROFILE_LIST,
        UserAllProfileResponse.from(
            userProfileListService.getProfiles(
                filter, limit, offset, search, generation, employed, orderBy, mbti, team)));
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

  @Override
  @GetMapping("/tl")
  public ResponseEntity<BaseResponse<?>> getTlMembers(@CurrentUserId Long userId) {
    List<TlMemberResponse> responses =
        appJamTlService.getCurrentGenerationTlMembers(userId).stream()
            .map(TlMemberResponse::from)
            .toList();
    return ResponseFactory.success(GET_TL_MEMBERS, responses);
  }

  @Override
  @GetMapping("/work-preference/recommendations")
  public ResponseEntity<BaseResponse<?>> getWorkPreferenceRecommendations(
      @CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_WORK_PREFERENCE_RECOMMENDATIONS,
        WorkPreferenceRecommendationResponse.from(
            userRecommendationService.getWorkPreferenceRecommendations(userId)));
  }

  @Override
  @GetMapping("/recommend/me")
  public ResponseEntity<BaseResponse<?>> getRecommendationsForMe(@CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_RECOMMENDATIONS,
        UserRecommendResponse.from(userRecommendationService.getRecommendationsForMe(userId)));
  }

  @Override
  @GetMapping("/recommend/{userId}")
  public ResponseEntity<BaseResponse<?>> getRecommendationsForUser(@PathVariable Long userId) {
    return ResponseFactory.success(
        GET_RECOMMENDATIONS,
        UserRecommendResponse.from(userRecommendationService.getRecommendationsForUser(userId)));
  }

  @Override
  @GetMapping("/recommend/me/generation-part")
  public ResponseEntity<BaseResponse<?>> getSameGenerationAndPartRecommendationsForMe(
      @CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_SAME_GENERATION_AND_PART_RECOMMENDATIONS,
        SameGenerationAndPartRecommendResponse.from(
            userRecommendationService.getSameGenerationAndPartRecommendations(userId)));
  }

  @Override
  @GetMapping("/recommend/{userId}/generation-part")
  public ResponseEntity<BaseResponse<?>> getSameGenerationAndPartRecommendationsForUser(
      @PathVariable Long userId) {
    return ResponseFactory.success(
        GET_SAME_GENERATION_AND_PART_RECOMMENDATIONS,
        SameGenerationAndPartRecommendResponse.from(
            userRecommendationService.getSameGenerationAndPartRecommendations(userId)));
  }

  @Override
  @GetMapping("/crew/{id}")
  public ResponseEntity<BaseResponse<?>> getUserCrew(
      @PathVariable Long id,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer take) {
    int pageNo = page == null ? DEFAULT_CREW_PAGE_NO : page;
    int limit = take == null ? DEFAULT_CREW_TAKE : take;
    return ResponseFactory.success(
        GET_MEMBER_CREW,
        MemberCrewResponse.from(userRecommendationService.getCrewMeetings(id, pageNo, limit)));
  }

  @Override
  @GetMapping("/ask/list")
  public ResponseEntity<BaseResponse<?>> getAskMembers(
      @RequestParam(required = false) String part) {
    return ResponseFactory.success(
        GET_ASK_MEMBERS, AskUserResponse.from(userAskQueryService.getAskTargetMembers(part)));
  }

  @Override
  @PatchMapping("/block/activate")
  public ResponseEntity<BaseResponse<?>> activateBlock(
      @Valid @RequestBody UserBlockRequest request, @CurrentUserId Long userId) {
    userRelationService.activateBlock(userId, request.blockedMemberId());
    return ResponseFactory.success(ACTIVATE_BLOCK, Map.of("유저 차단 활성 성공", true));
  }

  @Override
  @GetMapping("/block/{memberId}")
  public ResponseEntity<BaseResponse<?>> getBlockStatus(
      @PathVariable Long memberId, @CurrentUserId Long userId) {
    boolean status =
        userRelationService
            .getBlockStatus(userId, memberId)
            .map(UserBlock::isBlocked)
            .orElse(false);
    UserSummary blockingMember = userProfileQueryService.getMemberSummary(userId);
    UserSummary blockedMember = userProfileQueryService.getMemberSummary(memberId);
    return ResponseFactory.success(
        GET_BLOCK_STATUS, UserBlockResponse.of(status, blockingMember, blockedMember));
  }

  @Override
  @PostMapping("/report")
  public ResponseEntity<BaseResponse<?>> reportMember(
      @Valid @RequestBody UserReportRequest request, @CurrentUserId Long userId) {
    userRelationService.reportUser(userId, request.reportMemberId());
    return ResponseFactory.success(REPORT_MEMBER, Map.of("유저 신고 성공", true));
  }

  @Override
  @GetMapping("/property")
  public ResponseEntity<BaseResponse<?>> getMemberProperty(@CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_MEMBER_PROPERTY,
        UserPropertiesResponse.from(memberPropertyService.getMemberProperties(userId)));
  }

  private List<UserProfileCommandService.ActivityInput> toActivityInputs(
      List<UserProfileSaveRequest.MemberSoptActivitySaveRequest> activities) {
    if (activities == null) {
      return List.of();
    }
    return activities.stream()
        .map(a -> new UserProfileCommandService.ActivityInput(a.generation(), a.team()))
        .toList();
  }

  private List<UserProfileCommandService.ActivityInput> toActivityInputsFromUpdate(
      List<UserProfileUpdateRequest.MemberSoptActivityUpdateRequest> activities) {
    if (activities == null) {
      return List.of();
    }
    return activities.stream()
        .map(a -> new UserProfileCommandService.ActivityInput(a.generation(), a.team()))
        .toList();
  }

  private UserProfileCommandService.UserFavorInput toUserFavorInput(
      UserProfileSaveRequest.UserFavorRequest request) {
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
      UserProfileUpdateRequest.UserFavorRequest request) {
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
      UserProfileUpdateRequest.WorkPreferenceRequest request) {
    if (request == null) {
      return null;
    }
    return new UserProfileCommandService.WorkPreferenceInput(
        request.ideationStyle(),
        request.workTime(),
        request.communicationStyle(),
        request.workPlace(),
        request.feedbackStyle());
  }

  private UserProfileCommandService.WorkPreferenceInput toWorkPreferenceInput(
      WorkPreferenceUpdateRequest request) {
    return new UserProfileCommandService.WorkPreferenceInput(
        request.ideationStyle(),
        request.workTime(),
        request.communicationStyle(),
        request.workPlace(),
        request.feedbackStyle());
  }

  private List<UserProfileCommandService.LinkInput> toLinkInputs(
      List<UserProfileSaveRequest.MemberLinkSaveRequest> links) {
    if (links == null) {
      return List.of();
    }
    return links.stream()
        .map(l -> new UserProfileCommandService.LinkInput(null, l.title(), l.url()))
        .toList();
  }

  private List<UserProfileCommandService.LinkInput> toLinkInputsFromUpdate(
      List<UserProfileUpdateRequest.MemberLinkUpdateRequest> links) {
    if (links == null) {
      return List.of();
    }
    return links.stream()
        .map(l -> new UserProfileCommandService.LinkInput(l.id(), l.title(), l.url()))
        .toList();
  }

  private List<UserProfileCommandService.CareerInput> toCareerInputs(
      List<UserProfileSaveRequest.MemberCareerSaveRequest> careers) {
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
      List<UserProfileUpdateRequest.MemberCareerUpdateRequest> careers) {
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
