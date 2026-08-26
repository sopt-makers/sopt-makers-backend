package org.sopt.makers.clients.push;

import java.util.UUID;
import org.sopt.makers.clients.alarm.AlarmProperty;
import org.sopt.makers.clients.push.dto.PushServerResponse;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

class PushClient {

  private static final String ACTION_HEADER = "action";
  private static final String API_KEY_HEADER = "x-api-key";
  private static final String SERVICE_HEADER = "service";
  private static final String TRANSACTION_ID_HEADER = "transactionId";
  private static final String PLATFORM_HEADER = "platform";

  private final RestClient restClient;
  private final AlarmProperty property;

  PushClient(AlarmProperty property) {
    this.restClient = RestClient.builder().baseUrl(property.url()).build();
    this.property = property;
  }

  PushServerResponse send(String action, Object body) {
    return exchange(action, body, null);
  }

  PushServerResponse manageToken(String action, Object body, String platform) {
    return exchange(action, body, platform);
  }

  private PushServerResponse exchange(String action, Object body, String platform) {
    return restClient
        .post()
        .uri("")
        .contentType(MediaType.APPLICATION_JSON)
        .headers(
            headers -> {
              headers.add(ACTION_HEADER, action);
              headers.add(API_KEY_HEADER, property.key());
              headers.add(SERVICE_HEADER, property.appHeaderService());
              headers.add(TRANSACTION_ID_HEADER, UUID.randomUUID().toString());
              if (platform != null) {
                headers.add(PLATFORM_HEADER, platform);
              }
            })
        .body(body)
        .retrieve()
        .body(PushServerResponse.class);
  }
}
