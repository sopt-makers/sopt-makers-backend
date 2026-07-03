package org.sopt.makers.clients.crew;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.official.homepage.port.HomepageStudyCountPort;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrewStudyCountAdapter implements HomepageStudyCountPort {

  private final RestTemplate restTemplate;
  private final CrewProperty crewProperty;

  @Override
  public int getStudyCountByGeneration(Integer generationId) {
    String url = crewProperty.url() + "/internal/meeting/stats/studies?generation=" + generationId;

    StudyCountResponse response = restTemplate.getForObject(url, StudyCountResponse.class);
    if (response == null) {
      return 0;
    }
    return response.meetingCount();
  }

  private record StudyCountResponse(int meetingCount) {}
}
