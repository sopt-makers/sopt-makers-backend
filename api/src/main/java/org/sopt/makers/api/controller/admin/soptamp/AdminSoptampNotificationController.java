package org.sopt.makers.api.controller.admin.soptamp;

import static org.sopt.makers.api.controller.admin.soptamp.AdminSoptampSuccessCode.SUCCESS_SEND_SOPTAMP_SHOWCASE;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.admin.soptamp.dto.AdminSoptampNotificationRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.soptamp.facade.AdminSoptampFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/admin/notification")
public class AdminSoptampNotificationController implements AdminSoptampNotificationApi {

  private final AdminSoptampFacade adminSoptampFacade;
  private final AdminSoptampNotificationApiKeyVerifier apiKeyVerifier;

  @Override
  @PostMapping("/soptamp/showcase")
  public ResponseEntity<BaseResponse<?>> sendSoptampShowcase(
      @RequestHeader("apiKey") String apiKey,
      @Valid @RequestBody AdminSoptampNotificationRequest.Showcase request) {
    apiKeyVerifier.verify(apiKey);
    adminSoptampFacade.sendSoptampShowcase(
        request.missionId(),
        request.nickname(),
        request.notificationTitle(),
        request.notificationContent());
    return ResponseFactory.success(SUCCESS_SEND_SOPTAMP_SHOWCASE);
  }
}
