package org.sopt.makers.api.controller.app.soptamp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.sopt.makers.api.controller.app.soptamp.dto.StampRequest;
import org.sopt.makers.api.controller.app.soptamp.dto.StampResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "솝탬프 스탬프", description = "앱 솝탬프 스탬프 API")
public interface StampApi {

  @Operation(summary = "스탬프 조회", description = "닉네임의 유저가 해당 미션에 인증한 스탬프를 조회한다.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "스탬프 조회에 성공했습니다."),
    @ApiResponse(responseCode = "400", description = "존재하지 않는 스탬프입니다.", content = @Content),
    @ApiResponse(responseCode = "404", description = "존재하지 않는 유저입니다.", content = @Content)
  })
  ResponseEntity<BaseResponse<StampResponse.StampView>> getStamp(
      @Parameter(hidden = true) Long userId, @Valid StampRequest.FindStampRequest request);

  @Operation(summary = "스탬프 등록", description = "같은 미션에 이미 인증했으면 실패한다.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "스탬프 등록에 성공했습니다."),
    @ApiResponse(
        responseCode = "400",
        description = "스탬프 내용, 이미지, 활동 날짜, 미션 아이디 가운데 빠진 값이 있습니다.",
        content = @Content),
    @ApiResponse(
        responseCode = "404",
        description = "존재하지 않는 미션이거나, 솝탬프 유저가 존재하지 않습니다.",
        content = @Content),
    @ApiResponse(responseCode = "409", description = "이미 해당 미션에 대한 스탬프가 존재합니다.", content = @Content)
  })
  ResponseEntity<BaseResponse<StampResponse.StampMain>> registerStamp(
      @Parameter(hidden = true) Long userId, @Valid StampRequest.RegisterStampRequest request);

  @Operation(summary = "스탬프 수정")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "스탬프 수정에 성공했습니다."),
    @ApiResponse(
        responseCode = "400",
        description = "존재하지 않는 스탬프이거나, 스탬프 내용이나 이미지가 비어 있습니다.",
        content = @Content)
  })
  ResponseEntity<BaseResponse<StampResponse.StampId>> editStamp(
      @Parameter(hidden = true) Long userId, @Valid StampRequest.EditStampRequest request);

  @Operation(summary = "스탬프 삭제")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "스탬프 삭제에 성공했습니다."),
    @ApiResponse(
        responseCode = "403",
        description = "존재하지 않는 스탬프이거나, 자신의 스탬프가 아닙니다.",
        content = @Content),
    @ApiResponse(
        responseCode = "404",
        description = "스탬프에 걸린 미션이나 솝탬프 유저가 존재하지 않습니다.",
        content = @Content)
  })
  ResponseEntity<BaseResponse<Void>> deleteStamp(
      @Parameter(hidden = true) Long userId,
      @Parameter(description = "삭제할 스탬프 아이디", example = "1") Long stampId);

  @Operation(summary = "전체 스탬프 삭제")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "전체 스탬프 삭제에 성공했습니다."),
    @ApiResponse(responseCode = "404", description = "존재하지 않는 유저입니다.", content = @Content)
  })
  ResponseEntity<BaseResponse<Void>> deleteAllStamps(@Parameter(hidden = true) Long userId);

  @Operation(summary = "솝탬프 신고 URL 조회")
  @ApiResponse(responseCode = "200", description = "솝탬프 신고 URL 조회에 성공했습니다.")
  ResponseEntity<BaseResponse<StampResponse.SoptampReportResponse>> getReportUrl();
}
