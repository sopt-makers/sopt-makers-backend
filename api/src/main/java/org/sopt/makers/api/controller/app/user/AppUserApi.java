package org.sopt.makers.api.controller.app.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.app.user.dto.GenerationResponse;
import org.sopt.makers.api.controller.app.user.dto.MainViewResponse;
import org.sopt.makers.api.controller.app.user.dto.MySoptLogResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "앱 유저", description = "앱 메인 뷰와 기수 정보 API")
public interface AppUserApi {

  @Operation(summary = "메인 뷰 유저 정보 조회", description = "토큰이 없으면 UNAUTHENTICATED 기본값을 준다")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "메인 뷰 조회에 성공했습니다."),
    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다", content = @Content)
  })
  ResponseEntity<BaseResponse<MainViewResponse>> getMainViewInfo(
      @Parameter(hidden = true) Long userId);

  @Operation(summary = "현재 기수와 활동 상태 조회")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "기수 정보 조회에 성공했습니다."),
    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다", content = @Content)
  })
  ResponseEntity<BaseResponse<GenerationResponse>> getGenerationInfo(
      @Parameter(hidden = true) Long userId);

  @Operation(summary = "나의 솝트로그 조회")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "나의 솝트로그 조회에 성공했습니다."),
    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다", content = @Content)
  })
  ResponseEntity<BaseResponse<MySoptLogResponse>> getMySoptLog(
      @Parameter(hidden = true) Long userId);
}
