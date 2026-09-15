package org.sopt.makers.api.controller.app.soptamp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.sopt.makers.api.controller.app.soptamp.dto.AppjamRankResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamTeamSortType;
import org.springframework.http.ResponseEntity;

@Tag(name = "앱잼 랭킹", description = "앱 솝탬프 앱잼 랭킹 API")
public interface AppjamRankApi {

  @Operation(summary = "앱잼팀 랭킹 최근 인증한 미션 TOP 조회")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "앱잼팀 랭킹 최근 인증 미션 조회에 성공했습니다."),
    @ApiResponse(responseCode = "400", description = "size가 1 미만입니다.", content = @Content)
  })
  ResponseEntity<BaseResponse<AppjamRankResponse.AppjamtampRankListResponse>> getRecentTeamRanks(
      @Parameter(description = "가져올 개수", example = "3") @Min(1) int size);

  @Operation(summary = "앱잼팀 오늘의 득점 랭킹 조회")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "앱잼팀 오늘의 득점 랭킹 조회에 성공했습니다."),
    @ApiResponse(responseCode = "400", description = "size가 1 미만입니다.", content = @Content)
  })
  ResponseEntity<BaseResponse<AppjamRankResponse.AppjamTodayRankListResponse>> getTodayTeamRanks(
      @Parameter(description = "가져올 개수", example = "11") @Min(1) int size,
      @Parameter(description = "정렬 기준") AppjamTeamSortType sort);
}
