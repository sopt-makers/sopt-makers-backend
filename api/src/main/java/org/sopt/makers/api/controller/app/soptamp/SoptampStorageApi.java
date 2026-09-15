package org.sopt.makers.api.controller.app.soptamp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.app.soptamp.dto.PresignedUrlResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "솝탬프 S3", description = "앱 솝탬프 S3 API")
public interface SoptampStorageApi {

  @Operation(summary = "스탬프 pre-signed url 조회")
  @ApiResponse(responseCode = "200", description = "스탬프 pre-signed url 조회에 성공했습니다.")
  ResponseEntity<BaseResponse<PresignedUrlResponse>> getStampPreSignedUrl();

  @Operation(summary = "미션 pre-signed url 조회")
  @ApiResponse(responseCode = "200", description = "미션 pre-signed url 조회에 성공했습니다.")
  ResponseEntity<BaseResponse<PresignedUrlResponse>> getMissionPreSignedUrl();
}
