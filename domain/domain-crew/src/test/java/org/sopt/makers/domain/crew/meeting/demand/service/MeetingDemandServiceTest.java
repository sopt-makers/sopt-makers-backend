package org.sopt.makers.domain.crew.meeting.demand.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sopt.makers.domain.crew.meeting.MeetingUser;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemand;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandReport;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandStatus;
import org.sopt.makers.domain.crew.meeting.demand.exception.MeetingDemandException;
import org.sopt.makers.domain.crew.meeting.demand.notification.MeetingDemandNotification;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandCommentLikeRepositoryPort;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandCommentProfileRepositoryPort;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandCommentRepositoryPort;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandReportRepositoryPort;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandRepositoryPort;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandWaitHistoryRepositoryPort;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandWaitRepositoryPort;
import org.sopt.makers.domain.crew.meeting.port.MeetingRepositoryPort;
import org.sopt.makers.domain.crew.meeting.port.MeetingUserPort;
import org.sopt.makers.domain.crew.meeting.port.MemberRepositoryPort;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordType;

class MeetingDemandServiceTest {

  private final MeetingDemandRepositoryPort demandRepository =
      mock(MeetingDemandRepositoryPort.class);
  private final MeetingDemandWaitRepositoryPort waitRepository =
      mock(MeetingDemandWaitRepositoryPort.class);
  private final MeetingDemandWaitHistoryRepositoryPort waitHistoryRepository =
      mock(MeetingDemandWaitHistoryRepositoryPort.class);
  private final MeetingDemandCommentRepositoryPort commentRepository =
      mock(MeetingDemandCommentRepositoryPort.class);
  private final MeetingDemandCommentLikeRepositoryPort likeRepository =
      mock(MeetingDemandCommentLikeRepositoryPort.class);
  private final MeetingDemandCommentProfileRepositoryPort profileRepository =
      mock(MeetingDemandCommentProfileRepositoryPort.class);
  private final MeetingDemandReportRepositoryPort reportRepository =
      mock(MeetingDemandReportRepositoryPort.class);
  private final MeetingRepositoryPort meetingRepository = mock(MeetingRepositoryPort.class);
  private final MemberRepositoryPort memberRepository = mock(MemberRepositoryPort.class);
  private final MeetingUserPort userPort = mock(MeetingUserPort.class);
  private final MeetingDemandNotificationPublisher notificationPublisher =
      mock(MeetingDemandNotificationPublisher.class);

  private final MeetingDemandService service =
      new MeetingDemandService(
          demandRepository,
          waitRepository,
          waitHistoryRepository,
          commentRepository,
          likeRepository,
          profileRepository,
          reportRepository,
          meetingRepository,
          memberRepository,
          userPort,
          notificationPublisher);

  @Test
  @DisplayName("최초 기다려요는 이력을 남기고 수요 작성자에게 알림을 발행한다")
  void firstWaitCreatesHistoryAndPublishesNotification() {
    MeetingDemand demand = demand(1L);
    when(demandRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(demand));
    when(userPort.findById(2L)).thenReturn(Optional.of(new MeetingUser(2L, "유저", null, List.of())));
    when(waitRepository.existsByMeetingDemandIdAndUserId(10L, 2L)).thenReturn(false);
    when(waitHistoryRepository.existsByMeetingDemandIdAndUserId(10L, 2L)).thenReturn(false);
    when(waitRepository.countByMeetingDemandId(10L)).thenReturn(1L);
    when(demandRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    MeetingDemandService.WaitResult result = service.toggleWait(10L, 2L);

    assertThat(result.isWaiting()).isTrue();
    assertThat(result.waitCount()).isEqualTo(1);
    verify(waitHistoryRepository).save(any());
    ArgumentCaptor<MeetingDemandNotification> captor =
        ArgumentCaptor.forClass(MeetingDemandNotification.class);
    verify(notificationPublisher).publish(captor.capture());
    assertThat(captor.getValue().userIds()).containsExactly(1L);
    assertThat(captor.getValue().webPath()).isEqualTo("/suggest/detail?id=10");
  }

  @Test
  @DisplayName("기다려요를 다시 눌러도 작성자 알림은 반복하지 않는다")
  void repeatedWaitDoesNotRepublishNotification() {
    MeetingDemand demand = demand(1L);
    when(demandRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(demand));
    when(userPort.findById(2L)).thenReturn(Optional.of(new MeetingUser(2L, "유저", null, List.of())));
    when(waitRepository.existsByMeetingDemandIdAndUserId(10L, 2L)).thenReturn(false);
    when(waitHistoryRepository.existsByMeetingDemandIdAndUserId(10L, 2L)).thenReturn(true);
    when(waitRepository.countByMeetingDemandId(10L)).thenReturn(1L);
    when(demandRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    service.toggleWait(10L, 2L);

    verify(notificationPublisher, never()).publish(any());
    verify(waitHistoryRepository, never()).save(any());
  }

  @Test
  @DisplayName("작성자는 자신의 수요에 기다려요를 누를 수 없다")
  void writerCannotWaitOwnDemand() {
    when(demandRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(demand(1L)));

    assertThatThrownBy(() -> service.toggleWait(10L, 1L))
        .isInstanceOf(MeetingDemandException.class);
  }

  @Test
  @DisplayName("다른 사용자의 수요를 신고한다")
  void reportsAnotherUsersDemand() {
    when(demandRepository.findById(10L)).thenReturn(Optional.of(demand(1L)));
    when(reportRepository.existsByUserIdAndTarget(any(), any(), any())).thenReturn(false);
    when(reportRepository.save(any())).thenReturn(MeetingDemandReport.demand(2L, 10L));

    MeetingDemandReport report = service.reportMeetingDemand(10L, 2L);

    assertThat(report.userId()).isEqualTo(2L);
    assertThat(report.targetId()).isEqualTo(10L);
  }

  @Test
  @DisplayName("작성자는 자신의 수요를 신고할 수 없다")
  void writerCannotReportOwnDemand() {
    when(demandRepository.findById(10L)).thenReturn(Optional.of(demand(1L)));

    assertThatThrownBy(() -> service.reportMeetingDemand(10L, 1L))
        .isInstanceOf(MeetingDemandException.class);

    verify(reportRepository, never()).save(any());
  }

  @Test
  @DisplayName("이미 신고한 수요는 중복 신고할 수 없다")
  void duplicateDemandReportIsRejected() {
    when(demandRepository.findById(10L)).thenReturn(Optional.of(demand(1L)));
    when(reportRepository.existsByUserIdAndTarget(any(), any(), any())).thenReturn(true);

    assertThatThrownBy(() -> service.reportMeetingDemand(10L, 2L))
        .isInstanceOf(MeetingDemandException.class);

    verify(reportRepository, never()).save(any());
  }

  private MeetingDemand demand(Long writerId) {
    return new MeetingDemand(
        10L,
        writerId,
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
}
