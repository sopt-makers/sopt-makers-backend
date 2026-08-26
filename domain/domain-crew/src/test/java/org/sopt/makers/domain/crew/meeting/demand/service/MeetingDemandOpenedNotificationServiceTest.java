package org.sopt.makers.domain.crew.meeting.demand.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sopt.makers.domain.crew.meeting.Meeting;
import org.sopt.makers.domain.crew.meeting.MeetingCategory;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemand;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandOpenedNotification;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandStatus;
import org.sopt.makers.domain.crew.meeting.demand.notification.MeetingDemandNotification;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandCommentRepositoryPort;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandOpenedNotificationRepositoryPort;
import org.sopt.makers.domain.crew.meeting.demand.port.MeetingDemandWaitRepositoryPort;
import org.sopt.makers.domain.crew.meeting.port.MeetingRepositoryPort;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordType;

class MeetingDemandOpenedNotificationServiceTest {

  private static final Clock CLOCK =
      Clock.fixed(Instant.parse("2026-08-26T03:00:00Z"), ZoneId.of("Asia/Seoul"));

  private final MeetingDemandOpenedNotificationRepositoryPort notificationRepository =
      mock(MeetingDemandOpenedNotificationRepositoryPort.class);
  private final MeetingDemandWaitRepositoryPort waitRepository =
      mock(MeetingDemandWaitRepositoryPort.class);
  private final MeetingDemandCommentRepositoryPort commentRepository =
      mock(MeetingDemandCommentRepositoryPort.class);
  private final MeetingRepositoryPort meetingRepository = mock(MeetingRepositoryPort.class);
  private final MeetingDemandService demandService = mock(MeetingDemandService.class);
  private final MeetingDemandNotificationPublisher publisher =
      mock(MeetingDemandNotificationPublisher.class);

  private final MeetingDemandOpenedNotificationService service =
      new MeetingDemandOpenedNotificationService(
          notificationRepository,
          waitRepository,
          commentRepository,
          meetingRepository,
          demandService,
          publisher,
          CLOCK);

  @Test
  void 개설_알림은_수신자를_중복_제거하고_모임장을_제외한다() {
    Meeting meeting = meeting(3L);
    MeetingDemandOpenedNotification openedNotification =
        new MeetingDemandOpenedNotification(30L, 20L, null, null, null);
    when(notificationRepository.findByMeetingId(20L)).thenReturn(Optional.empty());
    when(notificationRepository.save(any())).thenReturn(openedNotification);
    when(demandService.getDemand(10L)).thenReturn(demand());
    when(commentRepository.findDistinctWriterUserIdsByMeetingDemandId(10L))
        .thenReturn(List.of(2L, 3L));
    when(waitRepository.findUserIdsByMeetingDemandId(10L)).thenReturn(List.of(1L, 2L));

    service.register(meeting);

    ArgumentCaptor<MeetingDemandNotification> notificationCaptor =
        ArgumentCaptor.forClass(MeetingDemandNotification.class);
    verify(publisher).publishOpened(notificationCaptor.capture(), any());
    assertThat(notificationCaptor.getValue().userIds()).containsExactly(1L, 2L);
    assertThat(notificationCaptor.getValue().webPath()).isEqualTo("/detail?id=20");
  }

  private MeetingDemand demand() {
    return new MeetingDemand(
        10L,
        1L,
        "러닝 모임",
        "함께 달리고 싶어요",
        MeetingDemandStatus.OPENED,
        "성실한 판다",
        1,
        List.of(MeetingKeywordType.EXERCISE),
        null,
        1,
        1,
        null,
        null);
  }

  private Meeting meeting(Long leaderId) {
    return new Meeting(
        20L,
        leaderId,
        10L,
        "러닝 모임",
        null,
        MeetingCategory.STUDY,
        List.of(),
        LocalDateTime.of(2026, 8, 25, 0, 0),
        LocalDateTime.of(2026, 8, 27, 0, 0),
        10,
        "설명",
        null,
        null,
        null,
        null,
        null,
        false,
        false,
        null,
        36,
        null,
        List.of(),
        null,
        null);
  }
}
