package org.sopt.makers.clients.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.clients.auth.dto.AuthUserCountResponse;
import org.sopt.makers.domain.official.homepage.port.HomepageMemberCountPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthMemberCountAdapter implements HomepageMemberCountPort {

  private final RestTemplate restTemplate;
  private final AuthProperty authProperty;

  @Override
  public int getMemberCountByGeneration(Integer generationId) {
    String url = authProperty.url() + "/api/v1/users/count?generation=" + generationId;

    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Api-Key", authProperty.apiKey());
    headers.set("X-Service-Name", authProperty.serviceName());

    ResponseEntity<AuthUserCountResponse> response =
        restTemplate.exchange(
            url, HttpMethod.GET, new HttpEntity<>(headers), AuthUserCountResponse.class);

    AuthUserCountResponse body = response.getBody();
    if (body == null || body.data() == null) {
      return 0;
    }
    return body.data().numberOfMembersAtGeneration();
  }
}
