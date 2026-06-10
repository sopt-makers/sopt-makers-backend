package org.sopt.makers.clients.sms;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.sopt.makers.clients.sms.exception.SmsException;
import org.sopt.makers.clients.sms.exception.SmsFailure;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class GabiaClient {

  private static final int SMS_MAX_LENGTH = 45;
  private static final int MAX_RETRY_COUNT = 3;

  private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private final GabiaSmsProperty property;
  private final Object accessTokenLock = new Object();
  private volatile CachedAccessToken cachedAccessToken;

  void send(final String phone, final String message) {
    String refkey = UUID.randomUUID().toString();
    for (int attempt = 1; attempt <= MAX_RETRY_COUNT; attempt++) {
      try {
        GabiaSmsResponse response = attemptSend(phone, message, refkey);
        if (GabiaApi.SUCCESS_CODE.equals(response.code())) {
          return;
        }
        log.warn("SMS 발송 실패, 재시도 {}/{}: {}", attempt, MAX_RETRY_COUNT, response.message());
      } catch (Exception e) {
        log.warn("SMS 발송 오류, 재시도 {}/{}: {}", attempt, MAX_RETRY_COUNT, e.getMessage());
      }
    }
    log.error("SMS 최종 발송 실패: phone={}", maskPhone(phone));
    throw new SmsException(SmsFailure.SMS_SEND_FAILED);
  }

  private String getAccessToken() throws IOException {
    CachedAccessToken token = cachedAccessToken;
    if (isAccessTokenUsable(token)) {
      return token.accessToken();
    }

    synchronized (accessTokenLock) {
      token = cachedAccessToken;
      if (isAccessTokenUsable(token)) {
        return token.accessToken();
      }

      GabiaAuthResponse response = requestAccessToken();
      cachedAccessToken = CachedAccessToken.from(response);
      return cachedAccessToken.accessToken();
    }
  }

  private boolean isAccessTokenUsable(final CachedAccessToken token) {
    return token != null && token.refreshAt().isAfter(Instant.now());
  }

  private GabiaAuthResponse requestAccessToken() throws IOException {
    String authValue = encodeBasicAuth(property.smsId(), property.apiKey());
    RequestBody body =
        new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(FormField.GRANT_TYPE, GabiaApi.CLIENT_CREDENTIALS_GRANT_TYPE)
            .build();
    Request request =
        new Request.Builder()
            .url(GabiaApi.OAUTH_TOKEN_URL)
            .post(body)
            .addHeader(HttpHeader.AUTHORIZATION, HttpHeader.BASIC_AUTH_PREFIX + authValue)
            .addHeader(HttpHeader.CACHE_CONTROL, HttpHeader.NO_CACHE)
            .build();
    try (Response response = HTTP_CLIENT.newCall(request).execute()) {
      if (!response.isSuccessful() || response.body() == null) {
        throw new IOException("Gabia 인증 실패: " + response.code());
      }
      return OBJECT_MAPPER.readValue(response.body().string(), GabiaAuthResponse.class);
    }
  }

  private GabiaSmsResponse attemptSend(
      final String phone, final String message, final String refkey) throws IOException {
    String accessToken = getAccessToken();
    String authValue = encodeBasicAuth(property.smsId(), accessToken);
    String url = message.length() <= SMS_MAX_LENGTH ? GabiaApi.SMS_SEND_URL : GabiaApi.LMS_SEND_URL;

    RequestBody body =
        new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(FormField.PHONE, phone)
            .addFormDataPart(FormField.CALLBACK, property.senderNumber())
            .addFormDataPart(FormField.MESSAGE, message)
            .addFormDataPart(FormField.REFKEY, refkey)
            .build();
    Request request =
        new Request.Builder()
            .url(url)
            .post(body)
            .addHeader(HttpHeader.AUTHORIZATION, HttpHeader.BASIC_AUTH_PREFIX + authValue)
            .addHeader(HttpHeader.CACHE_CONTROL, HttpHeader.NO_CACHE)
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
        .encodeToString(
            String.format(HttpHeader.BASIC_AUTH_FORMAT, id, secret)
                .getBytes(StandardCharsets.UTF_8));
  }

  private String maskPhone(final String phone) {
    if (phone == null || phone.isBlank()) {
      return "";
    }

    int digitCount = (int) phone.chars().filter(Character::isDigit).count();
    if (digitCount <= 4) {
      return "*".repeat(digitCount);
    }

    int visiblePrefixCount = Math.min(3, digitCount - 4);
    int visibleSuffixStart = digitCount - 4;
    StringBuilder masked = new StringBuilder(phone.length());
    int digitIndex = 0;

    for (char value : phone.toCharArray()) {
      if (!Character.isDigit(value)) {
        masked.append(value);
        continue;
      }

      if (digitIndex < visiblePrefixCount || digitIndex >= visibleSuffixStart) {
        masked.append(value);
      } else {
        masked.append('*');
      }
      digitIndex++;
    }
    return masked.toString();
  }

  private record CachedAccessToken(String accessToken, Instant refreshAt) {

    private static CachedAccessToken from(final GabiaAuthResponse response) {
      Duration ttl =
          response.expiresIn() > 0
              ? Duration.ofSeconds(response.expiresIn())
              : AccessTokenPolicy.DEFAULT_TTL;
      Duration cacheTtl =
          ttl.compareTo(AccessTokenPolicy.REFRESH_MARGIN) > 0
              ? ttl.minus(AccessTokenPolicy.REFRESH_MARGIN)
              : ttl.dividedBy(2);
      return new CachedAccessToken(response.accessToken(), Instant.now().plus(cacheTtl));
    }
  }

  private static final class GabiaApi {
    private static final String OAUTH_TOKEN_URL = "https://sms.gabia.com/oauth/token";
    private static final String SMS_SEND_URL = "https://sms.gabia.com/api/send/sms";
    private static final String LMS_SEND_URL = "https://sms.gabia.com/api/send/lms";
    private static final String CLIENT_CREDENTIALS_GRANT_TYPE = "client_credentials";
    private static final String SUCCESS_CODE = "200";
  }

  private static final class HttpHeader {
    private static final String AUTHORIZATION = "Authorization";
    private static final String CACHE_CONTROL = "cache-control";
    private static final String NO_CACHE = "no-cache";
    private static final String BASIC_AUTH_PREFIX = "Basic ";
    private static final String BASIC_AUTH_FORMAT = "%s:%s";
  }

  private static final class FormField {
    private static final String GRANT_TYPE = "grant_type";
    private static final String PHONE = "phone";
    private static final String CALLBACK = "callback";
    private static final String MESSAGE = "message";
    private static final String REFKEY = "refkey";
  }

  private static final class AccessTokenPolicy {
    private static final Duration REFRESH_MARGIN = Duration.ofMinutes(5);
    private static final Duration DEFAULT_TTL = Duration.ofHours(1);
  }
}
