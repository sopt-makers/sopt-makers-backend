package org.sopt.makers.api.controller.crew.meetingdemand;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.sopt.makers.api.controller.crew.CrewChannelMockMvc;
import org.sopt.makers.domain.crew.meeting.demand.service.MeetingDemandService;
import org.springframework.test.web.servlet.MockMvc;

class MeetingDemandControllerTest {

  private static final Long USER_ID = 10L;

  private final MeetingDemandService meetingDemandService = mock(MeetingDemandService.class);
  private final MockMvc mockMvc =
      CrewChannelMockMvc.of(new MeetingDemandController(meetingDemandService), USER_ID);

  @Test
  void 기다려요_토글은_수요와_인증_사용자를_전달하고_결과를_응답한다() throws Exception {
    when(meetingDemandService.toggleWait(3L, USER_ID))
        .thenReturn(new MeetingDemandService.WaitResult(5, true));

    mockMvc
        .perform(post("/meeting-demand/v2/3/wait"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.waitCount").value(5))
        .andExpect(jsonPath("$.data.isWaiting").value(true));

    verify(meetingDemandService).toggleWait(3L, USER_ID);
  }
}
