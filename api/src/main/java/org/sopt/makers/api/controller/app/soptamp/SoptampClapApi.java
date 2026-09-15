package org.sopt.makers.api.controller.app.soptamp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.app.soptamp.dto.AddClapRequest;
import org.sopt.makers.api.controller.app.soptamp.dto.AddClapResponse;
import org.sopt.makers.api.controller.app.soptamp.dto.ClapUserListResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "솝탬프 박수", description = "앱 솝탬프 박수 API")
public interface SoptampClapApi {

  @Operation(summary = "스탬프에 박수치기")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "스탬프 박수치기에 성공했습니다."),
    @ApiResponse(
        responseCode = "400",
        description = "잘못된 박수 횟수이거나, 존재하지 않는 스탬프입니다.",
        content = @Content),
    @ApiResponse(responseCode = "403", description = "타인의 스탬프에만 박수 칠 수 있습니다.", content = @Content)
  })
  ResponseEntity<BaseResponse<AddClapResponse>> addClap(
      @Parameter(hidden = true) Long userId,
      @Parameter(description = "박수를 칠 스탬프 아이디", example = "1") Long stampId,
      AddClapRequest request);

  @Operation(summary = "박수 친 유저 목록 조회 (본인 미션)")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "박수 친 유저 목록 조회에 성공했습니다."),
    @ApiResponse(responseCode = "400", description = "존재하지 않는 스탬프입니다.", content = @Content),
    @ApiResponse(
        responseCode = "403",
        description = "내 미션에서만 박수 목록을 조회할 수 있습니다.",
        content = @Content),
    @ApiResponse(responseCode = "404", description = "존재하지 않는 유저입니다.", content = @Content)
  })
  ResponseEntity<BaseResponse<ClapUserListResponse>> getClappers(
      @Parameter(hidden = true) Long userId,
      @Parameter(description = "스탬프 아이디", example = "1") Long stampId,
      @Parameter(description = "0부터 시작하는 페이지 번호. 음수를 주면 0으로 맞춘다", example = "0") int page,
      @Parameter(description = "한 페이지 크기. 1 미만이면 25로 맞춘다", example = "25") int size);
}
