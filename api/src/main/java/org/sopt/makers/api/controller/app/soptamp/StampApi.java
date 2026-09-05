package org.sopt.makers.api.controller.app.soptamp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.sopt.makers.api.controller.app.soptamp.dto.StampRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "솝탬프 스탬프", description = "앱 솝탬프 스탬프 API")
public interface StampApi {

  @Operation(summary = "스탬프 조회", description = "닉네임의 유저가 해당 미션에 인증한 스탬프를 조회한다.")
  ResponseEntity<BaseResponse<?>> getStamp(
      @Parameter(hidden = true) Long userId, @Valid StampRequest.FindStampRequest request);

  @Operation(summary = "스탬프 등록", description = "같은 미션에 이미 인증했으면 실패한다.")
  ResponseEntity<BaseResponse<?>> registerStamp(
      @Parameter(hidden = true) Long userId, @Valid StampRequest.RegisterStampRequest request);

  @Operation(summary = "스탬프 수정")
  ResponseEntity<BaseResponse<?>> editStamp(
      @Parameter(hidden = true) Long userId, @Valid StampRequest.EditStampRequest request);

  @Operation(summary = "스탬프 삭제")
  ResponseEntity<BaseResponse<?>> deleteStamp(@Parameter(hidden = true) Long userId, Long stampId);

  @Operation(summary = "전체 스탬프 삭제")
  ResponseEntity<BaseResponse<?>> deleteAllStamps(@Parameter(hidden = true) Long userId);

  @Operation(summary = "솝탬프 신고 URL 조회")
  ResponseEntity<BaseResponse<?>> getReportUrl();
}
