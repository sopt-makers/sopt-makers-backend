package org.sopt.makers.domain.official.homepage.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.homepage.port.HomepageUserCountPort;
import org.sopt.makers.domain.user.port.OfficialHomepageUserCountPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HomepageUserCountAdapter implements HomepageUserCountPort {

  private final OfficialHomepageUserCountPort officialHomepageUserCountPort;

  @Override
  public int getUserCountByGeneration(Integer generationId) {
    return officialHomepageUserCountPort.getUserCountByGeneration(generationId);
  }
}
