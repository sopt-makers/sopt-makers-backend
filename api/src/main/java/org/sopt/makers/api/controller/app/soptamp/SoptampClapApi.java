package org.sopt.makers.api.controller.app.soptamp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.app.soptamp.dto.AddClapRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "솝탬프 박수", description = "앱 솝탬프 박수 API")
public interface SoptampClapApi {

  @Operation(summary = "스탬프에 박수치기")
  ResponseEntity<BaseResponse<?>> addClap(
      @Parameter(hidden = true) Long userId, Long stampId, AddClapRequest request);

  @Operation(summary = "박수 친 유저 목록 조회 (본인 미션)")
  ResponseEntity<BaseResponse<?>> getClappers(
      @Parameter(hidden = true) Long userId, Long stampId, int page, int size);
}
