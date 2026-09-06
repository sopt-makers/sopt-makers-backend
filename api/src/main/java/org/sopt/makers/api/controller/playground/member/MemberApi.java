package org.sopt.makers.api.controller.playground.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.sopt.makers.api.controller.playground.member.dto.CheckActivityRequest;
import org.sopt.makers.api.controller.playground.member.dto.MemberProfileSaveRequest;
import org.sopt.makers.api.controller.playground.member.dto.MemberProfileUpdateRequest;
import org.sopt.makers.api.controller.playground.member.dto.WorkPreferenceUpdateRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Member 관련 API", description = "Member와 관련 API들")
@SecurityRequirement(name = "Authorization")
public interface MemberApi {

  @Operation(summary = "유저 id로 조회 API")
  ResponseEntity<BaseResponse<?>> getMember(Long id);

  @Operation(summary = "자신의 토큰으로 조회 API")
  ResponseEntity<BaseResponse<?>> getMyInfo(@Parameter(hidden = true) Long userId);

  @Operation(
      summary = "멤버 검색 API",
      description = "name 파라미터에 검색어가 있는 경우: 해당 이름이 포함된 유저를 최신 활동기수 순으로 정렬하여 반환합니다.")
  ResponseEntity<BaseResponse<?>> searchMember(String name);

  @Operation(
      summary = "유저 프로필 생성 API",
      description = "주량 : Double 0 -> 못마셔요 / 0.5 -> 0.5병 / 1.0 -> 1병 / 1.5 -> 1.5병 / 2.0 -> 2병 / 2.5 -> 2.5병 / 3.0 -> 3병 이상")
  ResponseEntity<BaseResponse<?>> createProfile(
      @Parameter(hidden = true) Long userId, @Valid MemberProfileSaveRequest request);

  @Operation(
      summary = "멤버 프로필 수정 API",
      description = "주량 : Double 0 -> 못마셔요 / 0.5 -> 0.5병 / 1.0 -> 1병 / 1.5 -> 1.5병 / 2.0 -> 2병 / 2.5 -> 2.5병 / 3.0 -> 3병 이상")
  ResponseEntity<BaseResponse<?>> updateProfile(
      @Parameter(hidden = true) Long userId, @Valid MemberProfileUpdateRequest request);

  @Operation(summary = "작업 성향 업데이트 API")
  ResponseEntity<BaseResponse<?>> updateWorkPreference(
      @Parameter(hidden = true) Long userId, WorkPreferenceUpdateRequest request);

  @Operation(summary = "멤버 작업 성향 조회 API")
  ResponseEntity<BaseResponse<?>> getWorkPreference(@Parameter(hidden = true) Long userId);

  @Operation(summary = "멤버 프로필 조회 API")
  ResponseEntity<BaseResponse<?>> getProfile(Long id, @Parameter(hidden = true) Long userId);

  @Operation(summary = "자신의 토큰으로 프로필 조회 API")
  ResponseEntity<BaseResponse<?>> getMyProfile(@Parameter(hidden = true) Long userId);

  @Operation(
      summary = "멤버 프로필 목록 조회 API",
      description =
          "filter/limit/offset/search/generation/employed/orderBy/mbti/team 조건으로 멤버 프로필 목록을 조회합니다.")
  ResponseEntity<BaseResponse<?>> getProfiles(
      Integer filter,
      Integer limit,
      Integer offset,
      String search,
      Integer generation,
      Integer employed,
      Integer orderBy,
      String mbti,
      String team);

  @Operation(summary = "본인 활동 기수 확인 여부 API", description = "해당 API를 호출하면 유저의 editActivitiesAble이 false로 바뀝니다")
  ResponseEntity<BaseResponse<?>> checkActivity(
      @Parameter(hidden = true) Long userId, @Valid CheckActivityRequest request);

  @Operation(summary = "멤버 프로필 링크 삭제 API")
  ResponseEntity<BaseResponse<?>> deleteProfileLink(Long linkId, @Parameter(hidden = true) Long userId);

  @Operation(
      summary = "앱잼 TL 멤버 조회 API",
      description = "최신 기수의 앱잼 TL로 참여한 멤버들을 이름 가나다순으로 조회합니다.")
  ResponseEntity<BaseResponse<?>> getTlMembers(@Parameter(hidden = true) Long userId);

  @Operation(summary = "작업 성향 유사 멤버 추천 API")
  ResponseEntity<BaseResponse<?>> getWorkPreferenceRecommendations(@Parameter(hidden = true) Long userId);

  @Operation(summary = "나와 유사한 멤버 추천 API")
  ResponseEntity<BaseResponse<?>> getRecommendationsForMe(@Parameter(hidden = true) Long userId);

  @Operation(summary = "특정 유저와 유사한 멤버 추천 API")
  ResponseEntity<BaseResponse<?>> getRecommendationsForUser(Long userId);

  @Operation(summary = "나와 동일 기수/파트 멤버 추천 API")
  ResponseEntity<BaseResponse<?>> getSameGenerationAndPartRecommendationsForMe(
      @Parameter(hidden = true) Long userId);

  @Operation(summary = "특정 유저와 동일 기수/파트 멤버 추천 API")
  ResponseEntity<BaseResponse<?>> getSameGenerationAndPartRecommendationsForUser(Long userId);

  @Operation(summary = "멤버 크루 조회 API")
  ResponseEntity<BaseResponse<?>> getUserCrew(Long id, Integer page, Integer take);
}
