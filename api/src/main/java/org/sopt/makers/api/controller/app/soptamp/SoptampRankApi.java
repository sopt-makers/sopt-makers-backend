package org.sopt.makers.api.controller.app.soptamp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.core.type.Part;
import org.springframework.http.ResponseEntity;

@Tag(name = "솝탬프 랭킹", description = "앱 솝탬프 랭킹 API")
public interface SoptampRankApi {

  @Operation(summary = "현재 기수 랭킹 목록 조회")
  ResponseEntity<BaseResponse<?>> findCurrentRanks();

  @Operation(summary = "파트 별 현재 기수 랭킹 목록 조회")
  ResponseEntity<BaseResponse<?>> findCurrentRanksByPart(Part part);

  @Operation(summary = "파트끼리의 랭킹 목록 조회")
  ResponseEntity<BaseResponse<?>> findPartRanks();

  @Operation(summary = "유저 미션 정보 상세 조회")
  ResponseEntity<BaseResponse<?>> findUserMissionsByNickname(String nickname);
}
