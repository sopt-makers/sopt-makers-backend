package org.sopt.makers.domain.playground.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.sopt.makers.domain.playground.post.exception.PostFailure.FORBIDDEN_MEETING_POST;

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
import org.sopt.makers.domain.playground.post.PostCategory;
import org.sopt.makers.domain.playground.post.PostContentType;
import org.sopt.makers.domain.playground.post.exception.PostException;
import org.sopt.makers.domain.playground.post.port.MeetingPostAccessPort;
import org.sopt.makers.domain.playground.post.port.MumuPostPolicyPort;
import org.sopt.makers.domain.playground.post.port.PostCommentRepositoryPort;
import org.sopt.makers.domain.playground.post.port.PostLikeRepositoryPort;
import org.sopt.makers.domain.playground.post.port.PostNotificationSenderPort;
import org.sopt.makers.domain.playground.post.port.PostReportRepositoryPort;
import org.sopt.makers.domain.playground.post.port.PostRepositoryPort;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.port.PlaygroundPostUserPort;

class PostServiceTest {

  private PostRepositoryPort postRepository;
  private MeetingPostAccessPort meetingAccess;
  private PlaygroundPostUserPort userPort;
  private PostService service;

  @BeforeEach
  void setUp() {
    postRepository = mock(PostRepositoryPort.class);
    meetingAccess = mock(MeetingPostAccessPort.class);
    userPort = mock(PlaygroundPostUserPort.class);
    service =
        new PostService(
            postRepository,
            mock(PostCommentRepositoryPort.class),
            mock(PostLikeRepositoryPort.class),
            mock(PostReportRepositoryPort.class),
            meetingAccess,
            userPort,
            mock(MumuPostPolicyPort.class),
            mock(PostNotificationSenderPort.class),
            Clock.fixed(Instant.parse("2026-09-03T00:00:00Z"), ZoneOffset.UTC));
  }

  @Test
  @DisplayName("모임 멤버는 MEETING 게시글을 작성할 수 있다")
  void createsMeetingPostForMember() {
    when(userPort.findWithActivitiesById(1L)).thenReturn(Optional.of(mock(User.class)));
    when(meetingAccess.findMeeting(10L, 1L)).thenReturn(Optional.of(meeting(true)));
    when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

    service.createMeetingPost(
        new PostService.CreateMeetingPostCommand(
            10L, "제목", "내용", List.of(), PostContentType.NORMAL),
        1L);

    ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
    verify(postRepository).save(captor.capture());
    assertThat(captor.getValue().category()).isEqualTo(PostCategory.MEETING);
    assertThat(captor.getValue().meetingId()).isEqualTo(10L);
  }

  @Test
  @DisplayName("모임 멤버가 아니면 게시글을 작성할 수 없다")
  void rejectsNonMember() {
    when(userPort.findWithActivitiesById(1L)).thenReturn(Optional.of(mock(User.class)));
    when(meetingAccess.findMeeting(10L, 1L)).thenReturn(Optional.of(meeting(false)));

    assertThatThrownBy(
            () ->
                service.createMeetingPost(
                    new PostService.CreateMeetingPostCommand(
                        10L, "제목", "내용", List.of(), PostContentType.NORMAL),
                    1L))
        .isInstanceOf(PostException.class)
        .extracting("error")
        .isEqualTo(FORBIDDEN_MEETING_POST);
  }

  private MeetingPostContext meeting(boolean member) {
    return new MeetingPostContext(10L, "모임", "스터디", List.of(), "설명", member);
  }
}
