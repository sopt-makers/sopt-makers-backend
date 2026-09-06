package org.sopt.makers.api.controller.app.home;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "홈", description = "앱 홈 화면 API")
public interface HomeApi {

  @Operation(summary = "홈 상단 활동 설명 조회")
  ResponseEntity<BaseResponse<?>> getHomeMainDescription(@Parameter(hidden = true) Long userId);

  @Operation(summary = "홈 앱 서비스 목록 조회", description = "토큰이 없으면 뱃지 없이 목록만 준다")
  ResponseEntity<BaseResponse<?>> getHomeAppService(@Parameter(hidden = true) Long userId);

  @Operation(summary = "탭 앱 서비스 목록 조회", description = "토큰이 없으면 뱃지 없이 목록만 준다")
  ResponseEntity<BaseResponse<?>> getTabAppService(@Parameter(hidden = true) Long userId);

  @Operation(summary = "플레이그라운드 최신 게시글 조회", description = "캐시가 비어 있으면 빈 목록을 주고 뒤에서 채운다")
  ResponseEntity<BaseResponse<?>> getRecentPosts();

  @Operation(summary = "플레이그라운드 인기 게시글 조회", description = "캐시가 비어 있으면 빈 목록을 주고 뒤에서 채운다")
  ResponseEntity<BaseResponse<?>> getPopularPosts();

  @Operation(summary = "플로팅 버튼 정보 조회", description = "토큰이 없으면 isActive 는 false 다")
  ResponseEntity<BaseResponse<?>> getFloatingButtonInfo(@Parameter(hidden = true) Long userId);

  @Operation(summary = "후기 폼 정보 조회", description = "토큰이 없으면 isActive 는 false 다")
  ResponseEntity<BaseResponse<?>> getReviewForm(@Parameter(hidden = true) Long userId);
}
