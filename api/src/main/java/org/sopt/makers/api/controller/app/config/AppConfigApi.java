package org.sopt.makers.api.controller.app.config;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.app.config.dto.AvailabilityResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "앱 설정", description = "앱 클라이언트가 기동 시 읽는 설정 API")
public interface AppConfigApi {

  @Operation(summary = "앱 메인 뷰 분기 처리")
  @ApiResponse(responseCode = "200", description = "앱 이용 가능 여부 조회에 성공했습니다.")
  ResponseEntity<BaseResponse<AvailabilityResponse>> getAvailability();
}
