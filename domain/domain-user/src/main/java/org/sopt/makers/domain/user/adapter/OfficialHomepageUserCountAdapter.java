package org.sopt.makers.domain.user.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.port.OfficialHomepageUserCountPort;
import org.sopt.makers.domain.user.service.UserQueryService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OfficialHomepageUserCountAdapter implements OfficialHomepageUserCountPort {

  private final UserQueryService userQueryService;

  @Override
  public int getUserCountByGeneration(Integer generationId) {
    return userQueryService.countByGenerationAndIsSopt(generationId, true);
  }
}
