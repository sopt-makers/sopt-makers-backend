package org.sopt.makers.domain.playground.post.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.playground.post.MeetingPostContext;
import org.sopt.makers.domain.playground.post.Post;
import org.sopt.makers.domain.playground.post.PostCategory;
import org.sopt.makers.domain.playground.post.PostContentType;
import org.sopt.makers.domain.playground.post.service.PostService;

class PlaygroundPostQueryAdapterTest {

  @Test
  @DisplayName("기존 Internal Post GET을 공통 게시글 Service 위임 Port로 제공한다")
  void delegatesMeetingFeedQuery() {
    PostService postService = mock(PostService.class);
    Post post =
        new Post(
            1L,
            10L,
            PostCategory.MEETING,
            PostContentType.NORMAL,
            20L,
            "제목",
            "내용",
            List.of(),
            0,
            0,
            0,
            false,
            false,
            false,
            false,
            null,
            null);
    PostService.PostView view =
        new PostService.PostView(
            post,
            null,
            new MeetingPostContext(20L, "모임", "스터디", List.of(), "설명", true),
            false,
            List.of(),
            false);
    when(postService.findMeetingPosts(null, 10L, 1, 12))
        .thenReturn(new PageResult<>(List.of(view), 1, 1, 1, 12, false, false));

    var result = new PlaygroundPostQueryAdapter(postService).findPosts(10L, 1, 12);

    assertThat(result.content())
        .singleElement()
        .satisfies(
            info -> {
              assertThat(info.post().id()).isEqualTo(1L);
              assertThat(info.meeting().meetingId()).isEqualTo(20L);
            });
  }
}
