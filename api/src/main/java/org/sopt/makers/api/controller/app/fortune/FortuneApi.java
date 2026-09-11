package org.sopt.makers.api.controller.app.fortune;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import org.sopt.makers.api.controller.app.fortune.dto.FortuneCardResponse;
import org.sopt.makers.api.controller.app.fortune.dto.FortuneResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "운세", description = "앱 운세(솝마디/운세카드) API")
public interface FortuneApi {

  @Operation(summary = "오늘의 솝마디 조회")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "오늘의 솝마디 조회에 성공했습니다."),
    @ApiResponse(
        responseCode = "404",
        description = "운세 ID에 해당하는 FortuneWord가 없습니다.",
        content = @Content)
  })
  ResponseEntity<BaseResponse<FortuneResponse>> getTodayFortuneWord(
      @Parameter(hidden = true) Long userId,
      @Parameter(description = "조회 기준 날짜", example = "2026-09-11") LocalDate todayDate);

  @Operation(summary = "오늘의 운세카드 조회")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "오늘의 운세카드 조회에 성공했습니다."),
    @ApiResponse(responseCode = "404", description = "유저에게 할당된 오늘의 운세가 없습니다.", content = @Content)
  })
  ResponseEntity<BaseResponse<FortuneCardResponse>> getTodayFortuneCard(
      @Parameter(hidden = true) Long userId);
}
