package org.sopt.makers.clients.sms;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class GabiaClient {

  private static final String SMS_OAUTH_TOKEN_URL = "https://sms.gabia.com/oauth/token";
  private static final String SMS_SEND_URL = "https://sms.gabia.com/api/send/sms";
  private static final String LMS_SEND_URL = "https://sms.gabia.com/api/send/lms";
  private static final int SMS_MAX_LENGTH = 45;
  private static final int MAX_RETRY_COUNT = 3;
  private static final String GABIA_SUCCESS_CODE = "200";

  private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private final GabiaSmsProperty property;

  void send(final String phone, final String message) {
    for (int attempt = 1; attempt <= MAX_RETRY_COUNT; attempt++) {
      try {
        GabiaSmsResponse response = attemptSend(phone, message);
        if (GABIA_SUCCESS_CODE.equals(response.code())) {
          return;
        }
        log.warn("SMS 발송 실패, 재시도 {}/{}: {}", attempt, MAX_RETRY_COUNT, response.message());
      } catch (Exception e) {
        log.warn("SMS 발송 오류, 재시도 {}/{}: {}", attempt, MAX_RETRY_COUNT, e.getMessage());
      }
    }
    log.error("SMS 최종 발송 실패: phone={}", phone);
    throw new IllegalStateException("SMS 발송 실패: 최대 재시도 횟수 초과");
  }

  private GabiaAuthResponse getAccessToken() throws IOException {
    String authValue = encodeBasicAuth(property.smsId(), property.apiKey());
    RequestBody body =
        new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("grant_type", "client_credentials")
            .build();
    Request request =
        new Request.Builder()
            .url(SMS_OAUTH_TOKEN_URL)
            .post(body)
            .addHeader("Authorization", "Basic " + authValue)
            .addHeader("cache-control", "no-cache")
            .build();
    try (Response response = HTTP_CLIENT.newCall(request).execute()) {
      if (!response.isSuccessful() || response.body() == null) {
        throw new IOException("Gabia 인증 실패: " + response.code());
      }
      return OBJECT_MAPPER.readValue(response.body().string(), GabiaAuthResponse.class);
    }
  }

  private GabiaSmsResponse attemptSend(final String phone, final String message)
      throws IOException {
    GabiaAuthResponse auth = getAccessToken();
    String authValue = encodeBasicAuth(property.smsId(), auth.accessToken());
    String url = message.length() <= SMS_MAX_LENGTH ? SMS_SEND_URL : LMS_SEND_URL;

    RequestBody body =
        new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("phone", phone)
            .addFormDataPart("callback", property.senderNumber())
            .addFormDataPart("message", message)
            .addFormDataPart("refkey", UUID.randomUUID().toString())
            .build();
    Request request =
        new Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Authorization", "Basic " + authValue)
            .addHeader("cache-control", "no-cache")
            .build();
    try (Response response = HTTP_CLIENT.newCall(request).execute()) {
      if (response.body() == null) {
        throw new IOException("Gabia 응답 없음");
      }
      return OBJECT_MAPPER.readValue(response.body().string(), GabiaSmsResponse.class);
    }
  }

  private String encodeBasicAuth(final String id, final String secret) {
    return Base64.getEncoder()
        .encodeToString(String.format("%s:%s", id, secret).getBytes(StandardCharsets.UTF_8));
  }
}
