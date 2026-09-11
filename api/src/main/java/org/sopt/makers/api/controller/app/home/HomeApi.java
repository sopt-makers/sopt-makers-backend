package org.sopt.makers.api.controller.app.home;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.sopt.makers.api.controller.app.home.dto.AppServiceEntryStatusResponse;
import org.sopt.makers.api.controller.app.home.dto.FloatingButtonResponse;
import org.sopt.makers.api.controller.app.home.dto.HomeAppServiceResponse;
import org.sopt.makers.api.controller.app.home.dto.HomeDescriptionResponse;
import org.sopt.makers.api.controller.app.home.dto.PlaygroundPopularPostsResponse;
import org.sopt.makers.api.controller.app.home.dto.PlaygroundRecentPostsResponse;
import org.sopt.makers.api.controller.app.home.dto.ReviewFormResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "홈", description = "앱 홈 화면 API")
public interface HomeApi {

  @Operation(summary = "홈 상단 활동 설명 조회")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "홈 설명 조회에 성공했습니다."),
    @ApiResponse(
        responseCode = "404",
        description = "존재하지 않는 회원이거나, 기수 정보를 찾을 수 없습니다.",
        content = @Content)
  })
  ResponseEntity<BaseResponse<HomeDescriptionResponse>> getHomeMainDescription(
      @Parameter(hidden = true) Long userId);

  @Operation(summary = "홈 앱 서비스 목록 조회", description = "토큰이 없으면 뱃지 없이 목록만 준다")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "홈 앱 서비스 조회에 성공했습니다."),
    @ApiResponse(
        responseCode = "404",
        description = "존재하지 않는 회원이거나, 앱 서비스 정보를 찾을 수 없습니다.",
        content = @Content)
  })
  ResponseEntity<BaseResponse<HomeAppServiceResponse>> getHomeAppService(
      @Parameter(hidden = true) Long userId);

  @Operation(summary = "탭 앱 서비스 목록 조회", description = "토큰이 없으면 뱃지 없이 목록만 준다")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "탭 앱 서비스 조회에 성공했습니다."),
    @ApiResponse(
        responseCode = "404",
        description = "존재하지 않는 회원이거나, 앱 서비스 정보를 찾을 수 없습니다.",
        content = @Content)
  })
  ResponseEntity<BaseResponse<List<AppServiceEntryStatusResponse>>> getTabAppService(
      @Parameter(hidden = true) Long userId);

  @Operation(summary = "플레이그라운드 최신 게시글 조회", description = "캐시가 비어 있으면 빈 목록을 주고 뒤에서 채운다")
  @ApiResponse(responseCode = "200", description = "최신 게시글 조회에 성공했습니다.")
  ResponseEntity<BaseResponse<PlaygroundRecentPostsResponse>> getRecentPosts();

  @Operation(summary = "플레이그라운드 인기 게시글 조회", description = "캐시가 비어 있으면 빈 목록을 주고 뒤에서 채운다")
  @ApiResponse(responseCode = "200", description = "인기 게시글 조회에 성공했습니다.")
  ResponseEntity<BaseResponse<PlaygroundPopularPostsResponse>> getPopularPosts();

  @Operation(summary = "플로팅 버튼 정보 조회", description = "토큰이 없으면 isActive 는 false 다")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "플로팅 버튼 조회에 성공했습니다."),
    @ApiResponse(
        responseCode = "404",
        description = "존재하지 않는 회원이거나, 앱 서비스 정보를 찾을 수 없습니다.",
        content = @Content)
  })
  ResponseEntity<BaseResponse<FloatingButtonResponse>> getFloatingButtonInfo(
      @Parameter(hidden = true) Long userId);

  @Operation(summary = "후기 폼 정보 조회", description = "토큰이 없으면 isActive 는 false 다")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "후기 폼 조회에 성공했습니다."),
    @ApiResponse(
        responseCode = "404",
        description = "존재하지 않는 회원이거나, 앱 서비스 정보를 찾을 수 없습니다.",
        content = @Content)
  })
  ResponseEntity<BaseResponse<ReviewFormResponse>> getReviewForm(
      @Parameter(hidden = true) Long userId);
}
