package org.sopt.makers.api.controller.app.soptamp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.sopt.makers.api.controller.app.soptamp.dto.AppjamtampRequest;
import org.sopt.makers.api.controller.app.soptamp.dto.AppjamtampResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.soptamp.appjam.TeamNumber;
import org.springframework.http.ResponseEntity;

@Tag(name = "앱잼탬프", description = "앱 솝탬프 앱잼 API")
public interface AppjamtampApi {

  @Operation(summary = "앱잼탬프 미션 목록 조회")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "앱잼탬프 미션 목록 조회에 성공했습니다."),
    @ApiResponse(
        responseCode = "404",
        description = "teamNumber로 지정한 팀이 존재하지 않습니다.",
        content = @Content)
  })
  ResponseEntity<BaseResponse<AppjamtampResponse.AppjamMissionResponses>> getMissions(
      @Parameter(hidden = true) Long userId,
      @Parameter(description = "조회할 팀 번호. 생략하면 내 팀을 조회하고, 소속 팀이 없으면 빈 팀 정보를 준다")
          TeamNumber teamNumber,
      @Parameter(description = "완료 여부로 거를 때 쓴다. 생략하면 전체", example = "true") Boolean isCompleted);

  @Operation(summary = "앱잼탬프 스탬프 조회")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "앱잼탬프 스탬프 조회에 성공했습니다."),
    @ApiResponse(responseCode = "400", description = "존재하지 않는 스탬프입니다.", content = @Content),
    @ApiResponse(responseCode = "404", description = "존재하지 않는 유저입니다.", content = @Content)
  })
  ResponseEntity<BaseResponse<AppjamtampResponse.AppjamtampView>> getStamp(
      @Parameter(hidden = true) Long userId, @Valid AppjamtampRequest.FindStampRequest request);

  @Operation(summary = "앱잼탬프 스탬프 제출")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "앱잼탬프 스탬프 제출에 성공했습니다."),
    @ApiResponse(
        responseCode = "400",
        description = "스탬프 내용, 이미지, 활동 날짜, 미션 아이디 가운데 빠진 값이 있습니다.",
        content = @Content),
    @ApiResponse(responseCode = "403", description = "앱잼에 참여하지 않은 유저입니다.", content = @Content),
    @ApiResponse(
        responseCode = "404",
        description = "존재하지 않는 미션이거나, 솝탬프 유저가 존재하지 않습니다.",
        content = @Content),
    @ApiResponse(responseCode = "409", description = "이미 해당 미션에 대한 스탬프가 존재합니다.", content = @Content)
  })
  ResponseEntity<BaseResponse<AppjamtampResponse.StampMain>> registerStamp(
      @Parameter(hidden = true) Long userId, @Valid AppjamtampRequest.RegisterStampRequest request);
}
