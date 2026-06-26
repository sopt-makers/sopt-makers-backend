package org.sopt.makers.api.controller.internal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.sopt.makers.api.controller.internal.dto.InternalScrapRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Internal", description = "내부 서비스용 API")
public interface InternalScrapApi {

  @Operation(summary = "블로그 링크 스크랩")
  ResponseEntity<BaseResponse<?>> scrap(@Valid InternalScrapRequest.Scrap request);
}
