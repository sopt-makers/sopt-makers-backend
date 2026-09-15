package org.sopt.makers.api.controller.crew.meetingdemand;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.sopt.makers.api.controller.crew.CrewChannelMockMvc;
import org.sopt.makers.domain.crew.meeting.demand.service.MeetingDemandCommentService;
import org.springframework.test.web.servlet.MockMvc;

class MeetingDemandCommentControllerTest {

  private static final Long USER_ID = 10L;

  private final MeetingDemandCommentService commentService =
      mock(MeetingDemandCommentService.class);
  private final MockMvc mockMvc =
      CrewChannelMockMvc.of(new MeetingDemandCommentController(commentService), USER_ID);

  @Test
  void 수요_댓글_삭제는_댓글과_인증_사용자를_전달한다() throws Exception {
    mockMvc
        .perform(delete("/meeting-demand/v2/comments/5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));

    verify(commentService).deleteComment(5L, USER_ID);
  }
}
