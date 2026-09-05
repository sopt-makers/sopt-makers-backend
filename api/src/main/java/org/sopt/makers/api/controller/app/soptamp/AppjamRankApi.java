package org.sopt.makers.api.controller.app.soptamp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamTeamSortType;
import org.springframework.http.ResponseEntity;

@Tag(name = "앱잼 랭킹", description = "앱 솝탬프 앱잼 랭킹 API")
public interface AppjamRankApi {

  @Operation(summary = "앱잼팀 랭킹 최근 인증한 미션 TOP 조회")
  ResponseEntity<BaseResponse<?>> getRecentTeamRanks(@Min(1) int size);

  @Operation(summary = "앱잼팀 오늘의 득점 랭킹 조회")
  ResponseEntity<BaseResponse<?>> getTodayTeamRanks(@Min(1) int size, AppjamTeamSortType sort);
}
