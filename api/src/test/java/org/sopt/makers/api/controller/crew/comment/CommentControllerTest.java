package org.sopt.makers.api.controller.crew.comment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.sopt.makers.api.controller.crew.CrewChannelMockMvc;
import org.sopt.makers.domain.playground.post.service.PostCommentService;
import org.springframework.test.web.servlet.MockMvc;

class CommentControllerTest {

  private static final Long USER_ID = 10L;

  private final PostCommentService commentService = mock(PostCommentService.class);
  private final MockMvc mockMvc =
      CrewChannelMockMvc.of(new CommentController(commentService), USER_ID);

  @Test
  void 댓글_삭제는_댓글과_인증_사용자를_전달한다() throws Exception {
    mockMvc
        .perform(delete("/comment/v2/5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));

    verify(commentService).deleteComment(5L, USER_ID);
  }
}
