package org.sopt.makers.api.controller.crew.user;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.sopt.makers.api.controller.crew.CrewChannelMockMvc;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordType;
import org.sopt.makers.domain.crew.meeting.tag.service.MeetingKeywordPreferenceService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class MeetingKeywordPreferenceControllerTest {

  private static final Long USER_ID = 10L;

  private final MeetingKeywordPreferenceService preferenceService =
      mock(MeetingKeywordPreferenceService.class);
  private final MockMvc mockMvc =
      CrewChannelMockMvc.of(new MeetingKeywordPreferenceController(preferenceService), USER_ID);

  @Test
  void 관심_키워드는_도메인_값으로_변환해_인증_사용자와_전달한다() throws Exception {
    mockMvc
        .perform(
            post("/user/v2/interestedKeywords")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"keywords\":[\"운동\",\"학습\"]}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));

    verify(preferenceService)
        .update(USER_ID, List.of(MeetingKeywordType.EXERCISE, MeetingKeywordType.STUDY));
  }
}
