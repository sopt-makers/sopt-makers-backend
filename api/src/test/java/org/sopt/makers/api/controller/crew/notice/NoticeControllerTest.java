package org.sopt.makers.api.controller.crew.notice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sopt.makers.api.controller.crew.CrewChannelMockMvc;
import org.sopt.makers.domain.crew.notice.service.NoticeService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class NoticeControllerTest {

  private final NoticeService noticeService = mock(NoticeService.class);
  private final MockMvc mockMvc =
      CrewChannelMockMvc.ofAnonymous(new NoticeController(noticeService));

  @Test
  void 공지_생성은_요청을_커맨드로_변환한다() throws Exception {
    mockMvc
        .perform(
            post("/notice/v2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "title": "점검 안내",
                      "subTitle": "서비스 점검",
                      "contents": "점검 예정입니다.",
                      "exposeStartDate": "2026-09-07T10:00:00",
                      "exposeEndDate": "2026-09-08T10:00:00",
                      "noticeSecretKey": "secret"
                    }
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true));

    ArgumentCaptor<NoticeService.CreateNoticeCommand> commandCaptor =
        ArgumentCaptor.forClass(NoticeService.CreateNoticeCommand.class);
    verify(noticeService).createNotice(commandCaptor.capture());
    assertThat(commandCaptor.getValue().title()).isEqualTo("점검 안내");
    assertThat(commandCaptor.getValue().noticeSecretKey()).isEqualTo("secret");
  }
}
