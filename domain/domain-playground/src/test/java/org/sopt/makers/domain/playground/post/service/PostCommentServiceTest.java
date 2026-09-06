package org.sopt.makers.domain.playground.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sopt.makers.domain.playground.post.MeetingPostContext;
import org.sopt.makers.domain.playground.post.Post;
import org.sopt.makers.domain.playground.post.PostContentType;
import org.sopt.makers.domain.playground.post.comment.PostComment;
import org.sopt.makers.domain.playground.post.port.MeetingPostAccessPort;
import org.sopt.makers.domain.playground.post.port.PostCommentLikeRepositoryPort;
import org.sopt.makers.domain.playground.post.port.PostCommentReportRepositoryPort;
import org.sopt.makers.domain.playground.post.port.PostCommentRepositoryPort;
import org.sopt.makers.domain.playground.post.port.PostNotificationSenderPort;
import org.sopt.makers.domain.playground.post.port.PostRepositoryPort;
import org.sopt.makers.domain.user.port.PlaygroundPostUserPort;

class PostCommentServiceTest {

  private PostRepositoryPort postRepository;
  private PostCommentRepositoryPort commentRepository;
  private MeetingPostAccessPort meetingAccess;
  private PostCommentService service;

  @BeforeEach
  void setUp() {
    postRepository = mock(PostRepositoryPort.class);
    commentRepository = mock(PostCommentRepositoryPort.class);
    meetingAccess = mock(MeetingPostAccessPort.class);
    service =
        new PostCommentService(
            postRepository,
            commentRepository,
            mock(PostCommentLikeRepositoryPort.class),
            mock(PostCommentReportRepositoryPort.class),
            meetingAccess,
            mock(PlaygroundPostUserPort.class),
            mock(PostNotificationSenderPort.class),
            Clock.fixed(Instant.parse("2026-09-03T00:00:00Z"), ZoneOffset.UTC));
  }

  @Test
  @DisplayName("댓글 삭제 시 행과 좋아요 수를 유지하고 작성자를 비식별화한다")
  void softDeletesComment() {
    PostComment comment = new PostComment(1L, 100L, 20L, "내용", null, 0, 0, 4, false, null, null);
    when(commentRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(comment));
    when(postRepository.findById(100L)).thenReturn(Optional.of(post()));
    when(meetingAccess.findMeeting(10L, 20L))
        .thenReturn(Optional.of(new MeetingPostContext(10L, "모임", "스터디", List.of(), "설명", true)));

    service.deleteComment(1L, 20L);

    ArgumentCaptor<PostComment> captor = ArgumentCaptor.forClass(PostComment.class);
    verify(commentRepository).save(captor.capture());
    assertThat(captor.getValue().isDeleted()).isTrue();
    assertThat(captor.getValue().writerId()).isNull();
    assertThat(captor.getValue().likeCount()).isEqualTo(4);
  }

  private Post post() {
    return new Post(
        100L,
        30L,
        org.sopt.makers.domain.playground.post.PostCategory.MEETING,
        PostContentType.NORMAL,
        10L,
        "제목",
        "내용",
        List.of(),
        0,
        1,
        0,
        false,
        false,
        false,
        false,
        null,
        null);
  }
}
