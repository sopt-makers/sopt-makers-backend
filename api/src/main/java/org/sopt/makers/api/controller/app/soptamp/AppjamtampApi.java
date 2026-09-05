package org.sopt.makers.api.controller.app.soptamp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.sopt.makers.api.controller.app.soptamp.dto.AppjamtampRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.soptamp.appjam.TeamNumber;
import org.springframework.http.ResponseEntity;

@Tag(name = "앱잼탬프", description = "앱 솝탬프 앱잼 API")
public interface AppjamtampApi {

  @Operation(summary = "앱잼탬프 미션 목록 조회")
  ResponseEntity<BaseResponse<?>> getMissions(
      @Parameter(hidden = true) Long userId, TeamNumber teamNumber, Boolean isCompleted);

  @Operation(summary = "앱잼탬프 스탬프 조회")
  ResponseEntity<BaseResponse<?>> getStamp(
      @Parameter(hidden = true) Long userId, @Valid AppjamtampRequest.FindStampRequest request);

  @Operation(summary = "앱잼탬프 스탬프 제출")
  ResponseEntity<BaseResponse<?>> registerStamp(
      @Parameter(hidden = true) Long userId, @Valid AppjamtampRequest.RegisterStampRequest request);
}
