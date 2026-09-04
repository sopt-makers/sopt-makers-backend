package org.sopt.makers.clients.crew;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.clients.crew.dto.CrewFastestAppliedGroupHttpResponse;
import org.sopt.makers.domain.playground.report.port.CrewReportClientPort;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrewReportClientAdapter implements CrewReportClientPort {

  private final RestTemplate restTemplate;
  private final CrewMeetingClientProperty crewMeetingClientProperty;

  @Override
  public List<String> getFastestAppliedGroupTitles(Long userId, int limit, int year) {
    try {
      String url =
          UriComponentsBuilder.fromUriString(crewMeetingClientProperty.url())
              .path("/internal/meeting/stats/fastest-applied/{orgId}")
              .queryParam("query-count", limit)
              .queryParam("query-year", year)
              .buildAndExpand(userId)
              .toUriString();

      CrewFastestAppliedGroupHttpResponse response =
          restTemplate.getForObject(url, CrewFastestAppliedGroupHttpResponse.class);

      if (response == null || response.topFastestAppliedMeetings() == null) {
        return List.of();
      }

      return response.topFastestAppliedMeetings().stream()
          .map(CrewFastestAppliedGroupHttpResponse.CrewGroupHttpDto::title)
          .toList();
    } catch (RestClientException e) {
      log.warn("Crew 빠른 가입 모임 통계 조회 실패. userId: {}", userId, e);
      return List.of();
    }
  }
}
