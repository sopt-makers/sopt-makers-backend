package org.sopt.makers.api.controller.official.health;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/official/health")
public class HealthCheckController implements HealthCheckApi {

  @Override
  @GetMapping
  public ResponseEntity<String> healthCheck() {
    return ResponseEntity.status(HttpStatus.OK).body("HealthCheck Complete");
  }

  @Override
  @GetMapping("/sentryerror")
  public void testSentryError() {
    throw new RuntimeException("Sentry 테스트 에러입니다!");
  }

  @Override
  @GetMapping("/sentryerror2")
  public void testSentryError2() {
    throw new RuntimeException("Sentry 테스트 에러입니다222222!");
  }

  @Override
  @GetMapping("/sentryerror3")
  public void testSentryError3() {
    throw new RuntimeException("Sentry 테스트 에러입니다3333333");
  }
}
