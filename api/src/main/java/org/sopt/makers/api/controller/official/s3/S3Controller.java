package org.sopt.makers.api.controller.official.s3;

import static org.sopt.makers.api.controller.official.s3.S3SuccessCode.CREATE_PRESIGNED_URL;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.official.s3.dto.S3Request;
import org.sopt.makers.api.controller.official.s3.dto.S3Response;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.official.news.port.NewsFileStoragePort;
import org.sopt.makers.domain.official.news.service.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "파일", description = "공식 홈페이지 파일 업로드 API")
@RequestMapping("/api/v1/s3")
@RequiredArgsConstructor
@Validated
public class S3Controller {

  private final NewsService newsService;

  @PostMapping("/presigned-url")
  public ResponseEntity<BaseResponse<?>> createPresignedUrl(
      @Valid @RequestBody S3Request.PresignedUrl request) {
    log.info(
        "Presigned URL 발급 요청 - fileName={}, contentType={}, directory={}",
        request.fileName(),
        request.contentType(),
        request.directory());

    NewsFileStoragePort.PresignedFile result =
        newsService.generatePresignedUrl(request.toCommand());
    return ResponseFactory.success(CREATE_PRESIGNED_URL, S3Response.PresignedUrl.of(result));
  }
}
