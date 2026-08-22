package org.sopt.makers.api.controller.official.s3;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.sopt.makers.api.controller.official.s3.dto.S3Request;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "파일", description = "공식 홈페이지 파일 업로드 API")
public interface S3Api {

  @Operation(summary = "Presigned URL 생성")
  ResponseEntity<BaseResponse<?>> createPresignedUrl(@Valid S3Request.PresignedUrl request);
}
