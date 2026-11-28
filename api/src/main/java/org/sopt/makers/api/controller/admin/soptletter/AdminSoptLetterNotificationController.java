package org.sopt.makers.api.controller.admin.soptletter;

import static org.sopt.makers.api.controller.admin.soptletter.AdminSoptLetterSuccessCode.SUCCESS_SEND_SOPT_LETTER_NEW_TOPIC;
import static org.sopt.makers.api.controller.admin.soptletter.AdminSoptLetterSuccessCode.SUCCESS_SEND_SOPT_LETTER_UNWRITTEN_REMINDER;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.admin.soptamp.AdminSoptampNotificationApiKeyVerifier;
import org.sopt.makers.api.controller.admin.soptletter.dto.AdminSoptLetterNotificationRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.soptletter.service.SoptLetterNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/admin/notification/sopt-letter")
public class AdminSoptLetterNotificationController implements AdminSoptLetterNotificationApi {

  private final SoptLetterNotificationService soptLetterNotificationService;
  private final AdminSoptampNotificationApiKeyVerifier apiKeyVerifier;

  @Override
  @PostMapping("/unwritten-reminder")
  public ResponseEntity<BaseResponse<?>> sendUnwrittenReminder(
      @RequestHeader("apiKey") String apiKey,
      @Valid @RequestBody AdminSoptLetterNotificationRequest request) {
    apiKeyVerifier.verify(apiKey);
    soptLetterNotificationService.sendUnwrittenReminder(
        request.notificationTitle(), request.notificationContent(), request.deepLink());
    return ResponseFactory.success(SUCCESS_SEND_SOPT_LETTER_UNWRITTEN_REMINDER);
  }

  @Override
  @PostMapping("/new-topic")
  public ResponseEntity<BaseResponse<?>> sendNewTopic(
      @RequestHeader("apiKey") String apiKey,
      @Valid @RequestBody AdminSoptLetterNotificationRequest request) {
    apiKeyVerifier.verify(apiKey);
    soptLetterNotificationService.sendNewTopic(
        request.notificationTitle(), request.notificationContent(), request.deepLink());
    return ResponseFactory.success(SUCCESS_SEND_SOPT_LETTER_NEW_TOPIC);
  }
}
