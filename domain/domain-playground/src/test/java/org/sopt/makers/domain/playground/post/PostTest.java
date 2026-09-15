package org.sopt.makers.domain.playground.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.sopt.makers.domain.playground.post.exception.PostFailure.INVALID_POST_CATEGORY;
import static org.sopt.makers.domain.playground.post.exception.PostFailure.TOO_MANY_POST_IMAGES;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.playground.post.exception.PostException;

class PostTest {

  @Test
  @DisplayName("모임 게시글은 MEETING 카테고리와 meetingId를 가진다")
  void createsMeetingPost() {
    Post post = Post.createMeetingPost(1L, 10L, "제목", "내용", List.of("image"), PostContentType.MUMU);

    assertThat(post.category()).isEqualTo(PostCategory.MEETING);
    assertThat(post.contentType()).isEqualTo(PostContentType.MUMU);
    assertThat(post.meetingId()).isEqualTo(10L);
    assertThat(post.isQuestion()).isFalse();
    assertThat(post.isHot()).isFalse();
  }

  @Test
  @DisplayName("MEETING 카테고리에 meetingId가 없으면 생성할 수 없다")
  void rejectsMeetingPostWithoutMeetingId() {
    assertThatThrownBy(
            () ->
                new Post(
                    null,
                    1L,
                    PostCategory.MEETING,
                    PostContentType.NORMAL,
                    null,
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
                    null))
        .isInstanceOf(PostException.class)
        .extracting("error")
        .isEqualTo(INVALID_POST_CATEGORY);
  }

  @Test
  @DisplayName("게시글 이미지는 최대 10개까지 등록할 수 있다")
  void rejectsTooManyImages() {
    List<String> images = Collections.nCopies(11, "image");

    assertThatThrownBy(
            () -> Post.createMeetingPost(1L, 10L, "제목", "내용", images, PostContentType.NORMAL))
        .isInstanceOf(PostException.class)
        .extracting("error")
        .isEqualTo(TOO_MANY_POST_IMAGES);
  }
}
