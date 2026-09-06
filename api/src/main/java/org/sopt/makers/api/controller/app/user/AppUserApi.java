package org.sopt.makers.api.controller.app.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "앱 유저", description = "앱 메인 뷰와 기수 정보 API")
public interface AppUserApi {

  @Operation(summary = "메인 뷰 유저 정보 조회", description = "토큰이 없으면 UNAUTHENTICATED 기본값을 준다")
  ResponseEntity<BaseResponse<?>> getMainViewInfo(@Parameter(hidden = true) Long userId);

  @Operation(summary = "현재 기수와 활동 상태 조회")
  ResponseEntity<BaseResponse<?>> getGenerationInfo(@Parameter(hidden = true) Long userId);
}
