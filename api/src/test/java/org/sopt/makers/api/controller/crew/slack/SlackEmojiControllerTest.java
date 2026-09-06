package org.sopt.makers.api.controller.crew.slack;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sopt.makers.api.controller.crew.CrewChannelMockMvc;
import org.sopt.makers.domain.crew.slack.service.SlackEmojiService;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

class SlackEmojiControllerTest {

  private static final String API_AUTH_TOKEN = "test-token";

  private final SlackEmojiService slackEmojiService = mock(SlackEmojiService.class);
  private final SlackEmojiController controller = new SlackEmojiController(slackEmojiService);
  private final MockMvc mockMvc = createMockMvc();

  @Test
  void 올바른_토큰이면_요청을_커맨드로_변환한다() throws Exception {
    mockMvc
        .perform(
            post("/slack/emoji")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody(API_AUTH_TOKEN)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));

    ArgumentCaptor<SlackEmojiService.AddMappingCommand> commandCaptor =
        ArgumentCaptor.forClass(SlackEmojiService.AddMappingCommand.class);
    verify(slackEmojiService).addMapping(commandCaptor.capture());
    assertThat(commandCaptor.getValue().callEmoji()).isEqualTo("wave");
    assertThat(commandCaptor.getValue().generation()).isEqualTo(38);
  }

  @Test
  void 잘못된_토큰이면_서비스를_호출하지_않는다() throws Exception {
    mockMvc
        .perform(
            post("/slack/emoji")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody("wrong-token")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false));

    verify(slackEmojiService, never()).addMapping(org.mockito.ArgumentMatchers.any());
  }

  private MockMvc createMockMvc() {
    ReflectionTestUtils.setField(controller, "apiAuthToken", API_AUTH_TOKEN);
    return CrewChannelMockMvc.ofAnonymous(controller);
  }

  private String requestBody(String token) {
    return """
        {
          "identifiedPwd": "%s",
          "callEmoji": "wave",
          "username": "홍길동",
          "userSlackId": "U123",
          "team": "SERVER",
          "generation": 38,
          "templateCd": "WELCOME"
        }
        """
        .formatted(token);
  }
}
