package org.sopt.makers.api.controller.official.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "HealthCheck", description = "헬스체크 및 Sentry 테스트 API")
public interface HealthCheckApi {

  @Operation(summary = "헬스체크")
  ResponseEntity<String> healthCheck();

  @Operation(summary = "Sentry 테스트 에러 1")
  void testSentryError();

  @Operation(summary = "Sentry 테스트 에러 2")
  void testSentryError2();

  @Operation(summary = "Sentry 테스트 에러 3")
  void testSentryError3();
}
