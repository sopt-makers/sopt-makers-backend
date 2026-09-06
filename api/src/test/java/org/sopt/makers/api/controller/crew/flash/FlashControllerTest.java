package org.sopt.makers.api.controller.crew.flash;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sopt.makers.api.controller.crew.CrewChannelMockMvc;
import org.sopt.makers.domain.crew.flash.FlashPlaceType;
import org.sopt.makers.domain.crew.flash.FlashTimingType;
import org.sopt.makers.domain.crew.flash.facade.FlashFacade;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class FlashControllerTest {

  private static final Long USER_ID = 10L;

  private final FlashFacade flashFacade = mock(FlashFacade.class);
  private final MockMvc mockMvc = CrewChannelMockMvc.of(new FlashController(flashFacade), USER_ID);

  @Test
  void 번쩍_수정은_요청을_커맨드로_변환하고_인증_사용자를_전달한다() throws Exception {
    mockMvc
        .perform(
            put("/flash/v2/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "flashBody": {
                        "title": "저녁 러닝",
                        "desc": "한강에서 달려요",
                        "flashTimingType": "당일",
                        "activityStartDate": "2026-09-07T19:00:00",
                        "activityEndDate": "2026-09-07T21:00:00",
                        "flashPlaceType": "오프라인",
                        "flashPlace": "여의나루역",
                        "minimumCapacity": 2,
                        "maximumCapacity": 8,
                        "files": ["https://example.com/run.png"]
                      },
                      "welcomeMessageTypes": ["초면 환영"],
                      "meetingKeywordTypes": ["운동"]
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));

    ArgumentCaptor<FlashFacade.UpdateFlashCommand> commandCaptor =
        ArgumentCaptor.forClass(FlashFacade.UpdateFlashCommand.class);
    verify(flashFacade)
        .updateFlash(
            org.mockito.ArgumentMatchers.eq(3L),
            commandCaptor.capture(),
            org.mockito.ArgumentMatchers.eq(USER_ID));
    FlashFacade.UpdateFlashCommand command = commandCaptor.getValue();
    assertThat(command.values().timingType()).isEqualTo(FlashTimingType.IMMEDIATE);
    assertThat(command.values().placeType()).isEqualTo(FlashPlaceType.OFFLINE);
    assertThat(command.values().activityStartDate()).isEqualTo(LocalDateTime.of(2026, 9, 7, 19, 0));
    assertThat(command.meetingKeywordTypes()).containsExactly(MeetingKeywordType.EXERCISE);
  }
}
