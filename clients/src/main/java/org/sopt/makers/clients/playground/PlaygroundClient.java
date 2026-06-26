package org.sopt.makers.clients.playground;

import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.clients.playground.dto.PlaygroundProjectDetailResponse;
import org.sopt.makers.clients.playground.dto.PlaygroundProjectPageResponse;
import org.springframework.web.client.RestClient;

@Slf4j
class PlaygroundClient {

  private final RestClient restClient;
  private final String token;

  PlaygroundClient(PlaygroundProperty property) {
    this.restClient = RestClient.builder().baseUrl(property.url()).build();
    this.token = property.token();
  }

  PlaygroundProjectPageResponse getAllProjects(int limit, int cursor) {
    return restClient
        .get()
        .uri("/api/v1/projects?limit={limit}&cursor={cursor}", limit, cursor)
        .retrieve()
        .body(PlaygroundProjectPageResponse.class);
  }

  PlaygroundProjectDetailResponse getProjectDetail(Long projectId) {
    return restClient
        .get()
        .uri("/internal/api/v1/projects/{projectId}", projectId)
        .header("Authorization", "Bearer " + token)
        .retrieve()
        .body(PlaygroundProjectDetailResponse.class);
  }
}
