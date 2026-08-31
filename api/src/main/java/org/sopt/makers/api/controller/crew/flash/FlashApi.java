package org.sopt.makers.api.controller.crew.flash;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.crew.flash.dto.CreateFlashRequest;
import org.sopt.makers.api.controller.crew.flash.dto.UpdateFlashRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "CREW 번쩍", description = "CREW 번쩍 모임 API")
public interface FlashApi {

  @Operation(summary = "번쩍 모임 생성")
  ResponseEntity<BaseResponse<?>> createFlash(
      CreateFlashRequest request, @Parameter(hidden = true) Long userId);

  @Operation(summary = "번쩍 모임 상세 조회")
  ResponseEntity<BaseResponse<?>> getFlash(Long meetingId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "번쩍 모임 수정")
  ResponseEntity<BaseResponse<?>> updateFlash(
      Long meetingId, UpdateFlashRequest request, @Parameter(hidden = true) Long userId);
}
