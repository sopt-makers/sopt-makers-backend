package org.sopt.makers.api.controller.crew.post;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.sopt.makers.api.controller.crew.CrewChannelMockMvc;
import org.sopt.makers.domain.playground.post.service.PostService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class PostControllerTest {

  private static final Long USER_ID = 10L;

  private final PostService postService = mock(PostService.class);
  private final MockMvc mockMvc = CrewChannelMockMvc.of(new PostController(postService), USER_ID);

  @Test
  void 게시글_개수_조회는_모임_ID를_전달하고_응답을_감싼다() throws Exception {
    when(postService.countMeetingPosts(3L)).thenReturn(7L);

    mockMvc
        .perform(get("/post/v2/count").param("meetingId", "3"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.postCount").value(7));

    verify(postService).countMeetingPosts(3L);
  }

  @Test
  void 사용자_멘션은_요청값과_인증_사용자를_전달한다() throws Exception {
    mockMvc
        .perform(
            post("/post/v2/mention")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"orgIds":[20,30],"postId":3,"content":"함께해요"}
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));

    verify(postService).mentionUsers(3L, List.of(20L, 30L), "함께해요", USER_ID);
  }
}
