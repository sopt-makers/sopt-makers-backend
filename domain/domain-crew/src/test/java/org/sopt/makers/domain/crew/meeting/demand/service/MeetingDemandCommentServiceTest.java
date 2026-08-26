package org.sopt.makers.domain.crew.meeting.demand.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sopt.makers.domain.crew.meeting.MeetingUser;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemand;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandReport;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandStatus;
import org.sopt.makers.domain.crew.meeting.demand.comment.MeetingDemandComment;
import org.sopt.makers.domain.crew.meeting.demand.comment.MeetingDemandCommentProfile;
import org.sopt.makers.domain.crew.meeting.demand.exception.MeetingDemandException;
import org.sopt.makers.domain.crew.meeting.demand.notification.MeetingDemandNotification;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandBlockedUserPort;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandCommentLikeRepositoryPort;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandCommentProfileRepositoryPort;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandCommentRepositoryPort;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandReportRepositoryPort;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandRepositoryPort;
import org.sopt.makers.domain.crew.meeting.port.MeetingUserPort;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordType;

class MeetingDemandCommentServiceTest {

  private static final Clock CLOCK =
      Clock.fixed(Instant.parse("2026-08-26T03:00:00Z"), ZoneId.of("Asia/Seoul"));

  private final MeetingDemandService demandService = mock(MeetingDemandService.class);
  private final MeetingDemandRepositoryPort demandRepository =
      mock(MeetingDemandRepositoryPort.class);
  private final MeetingDemandCommentRepositoryPort commentRepository =
      mock(MeetingDemandCommentRepositoryPort.class);
  private final MeetingDemandCommentLikeRepositoryPort likeRepository =
      mock(MeetingDemandCommentLikeRepositoryPort.class);
  private final MeetingDemandCommentProfileRepositoryPort profileRepository =
      mock(MeetingDemandCommentProfileRepositoryPort.class);
  private final MeetingDemandReportRepositoryPort reportRepository =
      mock(MeetingDemandReportRepositoryPort.class);
  private final MeetingDemandBlockedUserPort blockedUserPort =
      mock(MeetingDemandBlockedUserPort.class);
  private final MeetingUserPort userPort = mock(MeetingUserPort.class);
  private final MeetingDemandNotificationPublisher notificationPublisher =
      mock(MeetingDemandNotificationPublisher.class);

  private final MeetingDemandCommentService service =
      new MeetingDemandCommentService(
          demandService,
          demandRepository,
          commentRepository,
          likeRepository,
          profileRepository,
          reportRepository,
          blockedUserPort,
          userPort,
          notificationPublisher,
          CLOCK);

  @Test
  void 다른_사용자가_부모_댓글을_작성하면_수요_작성자에게_알림을_발행한다() {
    MeetingDemand demand = demand();
    when(demandService.getDemandForUpdate(10L)).thenReturn(demand);
    when(userPort.findById(2L)).thenReturn(Optional.of(new MeetingUser(2L, "유저", null, List.of())));
    when(profileRepository.findByMeetingDemandIdAndUserId(10L, 2L)).thenReturn(Optional.empty());
    when(profileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(commentRepository.save(any()))
        .thenAnswer(
            invocation -> {
              MeetingDemandComment comment = invocation.getArgument(0);
              return new MeetingDemandComment(
                  20L,
                  comment.meetingDemandId(),
                  comment.userId(),
                  comment.contents(),
                  comment.depth(),
                  comment.order(),
                  comment.parentId(),
                  comment.likeCount(),
                  null,
                  null);
            });
    when(demandRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    MeetingDemandComment result =
        service.createComment(
            10L, new MeetingDemandCommentService.CreateCommentCommand("좋아요", true, null), 2L);

    assertThat(result.id()).isEqualTo(20L);
    ArgumentCaptor<MeetingDemandNotification> captor =
        ArgumentCaptor.forClass(MeetingDemandNotification.class);
    verify(notificationPublisher).publish(captor.capture());
    assertThat(captor.getValue().userIds()).containsExactly(1L);
  }

  @Test
  void 대댓글은_다음_order로_생성하고_알림을_발행하지_않는다() {
    MeetingDemand demand = demand();
    MeetingDemandComment parent = parentComment(20L, 3L, "부모 댓글");
    when(demandService.getDemandForUpdate(10L)).thenReturn(demand);
    when(userPort.findById(2L)).thenReturn(Optional.of(new MeetingUser(2L, "유저", null, List.of())));
    when(profileRepository.findByMeetingDemandIdAndUserId(10L, 2L)).thenReturn(Optional.empty());
    when(profileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(commentRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(parent));
    when(commentRepository.findMaxReplyOrder(20L)).thenReturn(2);
    when(commentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(demandRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    MeetingDemandComment result =
        service.createComment(
            10L, new MeetingDemandCommentService.CreateCommentCommand("대댓글", false, 20L), 2L);

    assertThat(result.order()).isEqualTo(3);
    assertThat(result.parentId()).isEqualTo(20L);
    verify(notificationPublisher, never()).publish(any());
  }

  @Test
  @SuppressWarnings("unchecked")
  void 대댓글이_있는_부모_댓글은_삭제_표시하고_자식의_멘션을_지운다() {
    MeetingDemand demand = demand();
    MeetingDemandComment parent = parentComment(20L, 2L, "부모 댓글");
    MeetingDemandComment reply =
        new MeetingDemandComment(21L, 10L, 3L, "-~!@#@익명 유저[2]%^&*+ 답글", 1, 1, 20L, 0, null, null);
    MeetingDemandCommentProfile profile = MeetingDemandCommentProfile.create(10L, 2L, "익명 유저", 1);
    when(commentRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(parent));
    when(commentRepository.findById(20L)).thenReturn(Optional.of(parent));
    when(demandService.getDemandForUpdate(10L)).thenReturn(demand);
    when(commentRepository.findRepliesByParentId(20L)).thenReturn(List.of(reply));
    when(profileRepository.findByMeetingDemandIdAndUserId(10L, 2L))
        .thenReturn(Optional.of(profile));
    when(commentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(commentRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(demandRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    service.deleteComment(20L, 2L);

    ArgumentCaptor<List<MeetingDemandComment>> captor = ArgumentCaptor.forClass(List.class);
    verify(commentRepository).saveAll(captor.capture());
    assertThat(captor.getValue().get(0).contents()).isEqualTo("@_ 답글");
    verify(commentRepository, never()).delete(any());
  }

  @Test
  void 좋아요를_누르지_않은_댓글이면_좋아요를_추가한다() {
    MeetingDemandComment comment = parentComment(20L, 2L, "댓글");
    when(userPort.findById(3L)).thenReturn(Optional.of(new MeetingUser(3L, "유저", null, List.of())));
    when(commentRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(comment));
    when(likeRepository.existsByCommentIdAndUserId(20L, 3L)).thenReturn(false);
    when(commentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    boolean liked = service.switchCommentLike(20L, 3L);

    assertThat(liked).isTrue();
    verify(likeRepository).save(any());
    ArgumentCaptor<MeetingDemandComment> captor =
        ArgumentCaptor.forClass(MeetingDemandComment.class);
    verify(commentRepository).save(captor.capture());
    assertThat(captor.getValue().likeCount()).isEqualTo(1);
  }

  @Test
  void 다른_사용자의_댓글을_신고한다() {
    when(commentRepository.findById(20L)).thenReturn(Optional.of(parentComment(20L, 2L, "댓글")));
    when(reportRepository.existsByUserIdAndTarget(any(), any(), any())).thenReturn(false);
    when(reportRepository.save(any())).thenReturn(MeetingDemandReport.comment(3L, 20L));

    MeetingDemandReport report = service.reportComment(20L, 3L);

    assertThat(report.userId()).isEqualTo(3L);
    assertThat(report.targetId()).isEqualTo(20L);
  }

  @Test
  void 이미_신고한_댓글은_중복_신고할_수_없다() {
    when(commentRepository.findById(20L)).thenReturn(Optional.of(parentComment(20L, 2L, "댓글")));
    when(reportRepository.existsByUserIdAndTarget(any(), any(), any())).thenReturn(true);

    assertThatThrownBy(() -> service.reportComment(20L, 3L))
        .isInstanceOf(MeetingDemandException.class);

    verify(reportRepository, never()).save(any());
  }

  private MeetingDemand demand() {
    return new MeetingDemand(
        10L,
        1L,
        "러닝 모임",
        "함께 달리고 싶어요",
        MeetingDemandStatus.BEFORE_OPEN,
        "성실한 판다",
        1,
        List.of(MeetingKeywordType.EXERCISE),
        null,
        0,
        0,
        null,
        null);
  }

  private MeetingDemandComment parentComment(Long id, Long userId, String contents) {
    return new MeetingDemandComment(id, 10L, userId, contents, 0, 0, null, 0, null, null);
  }
}
