package org.sopt.makers.api.controller.app.soptamp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.sopt.makers.api.controller.app.soptamp.dto.PartRankResponse;
import org.sopt.makers.api.controller.app.soptamp.dto.RankDetailResponse;
import org.sopt.makers.api.controller.app.soptamp.dto.UserRankResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.core.type.Part;
import org.springframework.http.ResponseEntity;

@Tag(name = "솝탬프 랭킹", description = "앱 솝탬프 랭킹 API")
public interface SoptampRankApi {

  @Operation(summary = "현재 기수 랭킹 목록 조회")
  @ApiResponse(responseCode = "200", description = "현재 기수 랭킹 목록 조회에 성공했습니다.")
  ResponseEntity<BaseResponse<List<UserRankResponse>>> findCurrentRanks();

  @Operation(summary = "파트 별 현재 기수 랭킹 목록 조회")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "파트별 현재 기수 랭킹 목록 조회에 성공했습니다."),
    @ApiResponse(responseCode = "400", description = "솝탬프 랭킹을 제공하지 않는 파트입니다.", content = @Content)
  })
  ResponseEntity<BaseResponse<List<UserRankResponse>>> findCurrentRanksByPart(
      @Parameter(description = "조회할 파트") Part part);

  @Operation(summary = "파트끼리의 랭킹 목록 조회")
  @ApiResponse(responseCode = "200", description = "파트끼리의 랭킹 목록 조회에 성공했습니다.")
  ResponseEntity<BaseResponse<List<PartRankResponse>>> findPartRanks();

  @Operation(summary = "유저 미션 정보 상세 조회")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "유저 미션 정보 상세 조회에 성공했습니다."),
    @ApiResponse(responseCode = "404", description = "존재하지 않는 유저입니다.", content = @Content)
  })
  ResponseEntity<BaseResponse<RankDetailResponse>> findUserMissionsByNickname(
      @Parameter(description = "조회할 유저의 솝탬프 닉네임", example = "김앱짱") String nickname);
}
