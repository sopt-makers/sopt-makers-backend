package org.sopt.makers.api.controller.admin.soptamp;

import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AdminSoptampNotificationApiKeyVerifier {

  private final String apiKey;

  public AdminSoptampNotificationApiKeyVerifier(
      @Value("${sopt.soptamp.admin.notification-api-key}") String apiKey) {
    this.apiKey = apiKey;
  }

  public void verify(String requestApiKey) {
    if (apiKey == null || apiKey.isBlank() || !apiKey.equals(requestApiKey)) {
      throw new SoptampException(SoptampFailure.INVALID_SOPTAMP_NOTIFICATION_API_KEY);
    }
  }
}
