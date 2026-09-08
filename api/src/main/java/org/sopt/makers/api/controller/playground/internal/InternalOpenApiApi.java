package org.sopt.makers.api.controller.playground.internal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.sopt.makers.api.controller.playground.internal.dto.CreateDefaultUserProfileRequest;
import org.sopt.makers.api.controller.playground.internal.dto.InternalLatestPostResponse;
import org.sopt.makers.api.controller.playground.internal.dto.InternalMemberProfileListResponse;
import org.sopt.makers.api.controller.playground.internal.dto.InternalMemberProfileResponse;
import org.sopt.makers.api.controller.playground.internal.dto.InternalMemberProjectResponse;
import org.sopt.makers.api.controller.playground.internal.dto.InternalPopularPostResponse;
import org.sopt.makers.api.controller.playground.internal.dto.InternalRecommendMemberListRequest;
import org.sopt.makers.api.controller.playground.internal.dto.InternalRecommendMemberListResponse;
import org.sopt.makers.api.controller.playground.project.dto.ProjectDetailResponse;
import org.springframework.http.ResponseEntity;

/**
 * 레거시 sopt-playground-backend의 InternalOpenApiController(/internal/api/v1)를 100% 호환 유지하기 위해 제공되는
 * Internal API이다.
 *
 * <p>앱팀/플랫폼팀 등 타 팀이 아직 이 엔드포인트를 호출하고 있어 URL/HTTP Method/응답 스펙을 변경 없이 그대로 유지한다. 다만 내부 구현은 헥사고날
 * 아키텍처(Controller → Domain Service → Port/Adapter)로 재구성되어 있으므로, 새로 이 데이터가 필요한 타 팀/타 기능은 가능하면 이
 * 엔드포인트를 호출하는 대신 해당 도메인이 노출하는 Port(예: {@code PlaygroundProfileUserPort}, {@code
 * PlaygroundRecommendationUserPort})를 직접 주입받아 사용하는 것을 권장한다.
 */
@Tag(name = "내부 서비스 오픈 API")
public interface InternalOpenApiApi {

  @Operation(summary = "Project id로 조회 - 공홈")
  ResponseEntity<ProjectDetailResponse> getProject(Long id);

  @Operation(summary = "솝트에서 진행한 프로젝트 개수 조회 - 앱팀")
  ResponseEntity<InternalMemberProjectResponse> getMemberProject(
      @Parameter(description = "조회할 멤버 ID", example = "1") Long memberId);

  @Operation(summary = "커뮤니티 최신글 5개 조회 - 앱팀", description = "최상위 카테고리별(자유, 홍보, 솝티클)로 최신글 1개씩 조회")
  ResponseEntity<List<InternalLatestPostResponse>> getLatestPostsForApp();

  @Operation(summary = "커뮤니티 인기글 3 조회 - 앱팀")
  ResponseEntity<List<InternalPopularPostResponse>> getPopularPosts();

  @Operation(summary = "단일 멤버 ID별 상세 프로필 조회 - 앱팀")
  ResponseEntity<InternalMemberProfileResponse> getUserProfile(
      @Parameter(description = "조회할 멤버 ID", example = "1") String memberId);

  @Operation(summary = "다중 멤버 ID별 프로필 조회 - 앱팀")
  ResponseEntity<List<InternalMemberProfileListResponse>> getUserProfileList(
      @Parameter(description = "조회할 멤버 ID 목록", example = "1,2") String memberIds);

  @Operation(
      summary = "프로필 정보 기반 추천 친구 목록 조회 API - 앱팀",
      description =
          """
          key 필드는 유효한 추천 필터 값이 들어가야 함
          - MBTI
          - UNIVERSITY
          """)
  ResponseEntity<InternalRecommendMemberListResponse> getMyRecommendList(
      @Valid InternalRecommendMemberListRequest request);

  @Operation(summary = "기본 유저 프로필 생성 - 플랫폼팀")
  ResponseEntity<String> createUserProfile(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "플그 기본 유저 프로필 생성 요청",
              required = true,
              content =
                  @Content(
                      schema = @Schema(implementation = CreateDefaultUserProfileRequest.class)))
          CreateDefaultUserProfileRequest request,
      String apiKey);

  @Operation(summary = "기본 유저 프로필 삭제 - 플랫폼팀")
  ResponseEntity<String> deleteUserProfile(Long memberId, String apiKey);
}
