package org.sopt.makers.api.controller.playground.internal;

import jakarta.validation.Valid;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.api.controller.playground.internal.dto.CreateDefaultUserProfileRequest;
import org.sopt.makers.api.controller.playground.internal.dto.InternalLatestPostResponse;
import org.sopt.makers.api.controller.playground.internal.dto.InternalMemberProfileListResponse;
import org.sopt.makers.api.controller.playground.internal.dto.InternalMemberProfileResponse;
import org.sopt.makers.api.controller.playground.internal.dto.InternalMemberProjectResponse;
import org.sopt.makers.api.controller.playground.internal.dto.InternalPopularPostResponse;
import org.sopt.makers.api.controller.playground.internal.dto.InternalRecommendMemberListRequest;
import org.sopt.makers.api.controller.playground.internal.dto.InternalRecommendMemberListResponse;
import org.sopt.makers.api.controller.playground.internal.dto.SearchContent;
import org.sopt.makers.api.controller.playground.project.dto.ProjectDetailResponse;
import org.sopt.makers.domain.playground.community.post.service.CommunityPostQueryService;
import org.sopt.makers.domain.playground.member.profile.service.UserProfileQueryService;
import org.sopt.makers.domain.playground.member.profile.service.UserRecommendationService;
import org.sopt.makers.domain.playground.project.service.ProjectService;
import org.sopt.makers.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * 레거시 sopt-playground-backend의 InternalOpenApiController를 대체하는 레거시 호환성용 Internal API이며, 향후 타 팀 상황에
 * 따라 직접 도메인 Port를 주입받아 쓰거나 이 엔드포인트를 계속 호출할 수 있다.
 *
 * <p>기본 유저 프로필 생성/삭제(POST /members, DELETE /members/{memberId})는 과거 Platform/Playground가 분리된 서비스였을
 * 때 Playground 쪽에 shadow 프로필 행을 만들기 위한 API였다. 병합된 이 백엔드에서는 유저가 단일 users 테이블에 이미 완전한 상태로 존재하므로(가입
 * 시점에 name/phone 등이 모두 채워짐, id는 IDENTITY 채번) 더 이상 "빈 프로필 행"을 만들거나 지울 수 없다. 따라서 두 엔드포인트는 호출자 호환을 위해
 * URL/HTTP Status/응답 문구는 그대로 유지하되, 실제로는 존재 여부만 확인하고 DB를 변경하지 않는 no-op으로 동작한다.
 */
@Slf4j
@RestController
@RequestMapping("/internal/api/v1")
@RequiredArgsConstructor
public class InternalOpenApiController implements InternalOpenApiApi {

  private final ProjectService projectService;
  private final CommunityPostQueryService communityPostQueryService;
  private final UserProfileQueryService userProfileQueryService;
  private final UserRecommendationService userRecommendationService;

  @Value("${internal.platform.api-key:}")
  private String internalPlatformApiKey;

  @Override
  @GetMapping("/projects/{id}")
  public ResponseEntity<ProjectDetailResponse> getProject(@PathVariable Long id) {
    ProjectDetailResponse response =
        ProjectDetailResponse.from(projectService.getProjectDetail(id));
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @Override
  @GetMapping("/members/{memberId}/project")
  public ResponseEntity<InternalMemberProjectResponse> getMemberProject(
      @PathVariable Long memberId) {
    int count = projectService.getProjectCountByMemberId(memberId);
    return ResponseEntity.status(HttpStatus.OK).body(new InternalMemberProjectResponse(count));
  }

  @Override
  @GetMapping("/community/posts/latest")
  public ResponseEntity<List<InternalLatestPostResponse>> getLatestPostsForApp() {
    List<InternalLatestPostResponse> response =
        communityPostQueryService.getInternalLatestPosts().stream()
            .map(InternalLatestPostResponse::from)
            .toList();
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @Override
  @GetMapping("/community/posts/popular")
  public ResponseEntity<List<InternalPopularPostResponse>> getPopularPosts() {
    int limit = 3;
    List<InternalPopularPostResponse> response =
        communityPostQueryService.getInternalPopularPosts(limit).stream()
            .map(InternalPopularPostResponse::from)
            .toList();
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @Override
  @GetMapping("/members/profile/me")
  public ResponseEntity<InternalMemberProfileResponse> getUserProfile(
      @RequestParam String memberId) {
    User user = userProfileQueryService.getMemberProfileForInternalApi(Long.valueOf(memberId));
    return ResponseEntity.status(HttpStatus.OK).body(InternalMemberProfileResponse.from(user));
  }

  @Override
  @GetMapping("/members/profile")
  public ResponseEntity<List<InternalMemberProfileListResponse>> getUserProfileList(
      @RequestParam String memberIds) {
    List<Long> userIds =
        Arrays.stream(URLDecoder.decode(memberIds, StandardCharsets.UTF_8).split(","))
            .filter(s -> !s.isBlank())
            .map(Long::valueOf)
            .toList();

    List<InternalMemberProfileListResponse> response =
        userProfileQueryService.getMemberProfileListForInternalApi(userIds).stream()
            .map(InternalMemberProfileListResponse::from)
            .toList();
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @Override
  @PostMapping("/members/profile/recommend")
  public ResponseEntity<InternalRecommendMemberListResponse> getMyRecommendList(
      @RequestBody @Valid InternalRecommendMemberListRequest request) {
    Set<Long> memberIds =
        userRecommendationService.getRecommendedMemberIdsByGenerationAndFilter(
            request.generations(),
            request.getValueByKey(SearchContent.UNIVERSITY),
            request.getValueByKey(SearchContent.MBTI));
    return ResponseEntity.ok(new InternalRecommendMemberListResponse(memberIds));
  }

  @Override
  @PostMapping("/members")
  public ResponseEntity<String> createUserProfile(
      @RequestBody CreateDefaultUserProfileRequest request,
      @RequestHeader("apiKey") String apiKey) {
    validateApiKey(apiKey);

    Long userId = request.userId();
    if (userProfileQueryService.existsMember(userId)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 유저입니다. userId=" + userId);
    }
    return ResponseEntity.status(HttpStatus.CREATED)
        .body("기본 유저 프로필이 성공적으로 생성되었습니다. user id: " + userId);
  }

  @Override
  @DeleteMapping("/members/{memberId}")
  public ResponseEntity<String> deleteUserProfile(
      @PathVariable Long memberId, @RequestHeader("apiKey") String apiKey) {
    validateApiKey(apiKey);

    if (!userProfileQueryService.existsMember(memberId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 id의 Member를 찾을 수 없습니다.");
    }
    return ResponseEntity.status(HttpStatus.OK)
        .body("기본 유저 프로필이 성공적으로 삭제되었습니다. user id: " + memberId);
  }

  private void validateApiKey(String providedApiKey) {
    if (!Objects.equals(internalPlatformApiKey, providedApiKey)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "잘못된 api key 입니다.");
    }
  }

  /**
   * 이 컨트롤러 내부에서만 적용되는 로컬 핸들러. {@code GlobalExceptionHandler}(BaseResponse 공통 규격)를 거치지 않고 이 클래스 안에서
   * 던진 {@link ResponseStatusException}(apiKey 검증 실패 401, 중복 생성 409, 존재하지 않는 유저 삭제 404 등)만 격리해서 처리한다
   * — {@code Exception.class} catch-all에 가로채져 500으로 뭉개지는 것을 막되, 다른 도메인의 전역 에러 규격에는 영향을 주지 않는다.
   */
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<String> handleResponseStatusException(final ResponseStatusException e) {
    log.warn(e.getMessage());
    return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
  }
}
