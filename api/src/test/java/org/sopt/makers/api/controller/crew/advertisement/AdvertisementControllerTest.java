package org.sopt.makers.api.controller.crew.advertisement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.sopt.makers.api.controller.crew.CrewChannelMockMvc;
import org.sopt.makers.domain.crew.advertisement.AdvertisementEventType;
import org.sopt.makers.domain.crew.advertisement.service.AdvertisementService;
import org.springframework.test.web.servlet.MockMvc;

class AdvertisementControllerTest {

  private static final Long USER_ID = 10L;

  private final AdvertisementService advertisementService = mock(AdvertisementService.class);
  private final MockMvc mockMvc =
      CrewChannelMockMvc.of(new AdvertisementController(advertisementService), USER_ID);

  @Test
  void 상단_광고_조회는_이벤트와_인증_사용자를_전달한다() throws Exception {
    when(advertisementService.getMeetingTopAdvertisement(USER_ID, AdvertisementEventType.SOPKATHON))
        .thenReturn(Optional.empty());

    mockMvc
        .perform(get("/advertisement/v2/meeting/top").param("eventType", "SOPKATHON"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.isDisplay").value(false));

    verify(advertisementService)
        .getMeetingTopAdvertisement(USER_ID, AdvertisementEventType.SOPKATHON);
  }
}
