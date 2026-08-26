package org.sopt.makers.domain.crew.flash.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sopt.makers.domain.crew.flash.Flash;
import org.sopt.makers.domain.crew.flash.FlashPlaceType;
import org.sopt.makers.domain.crew.flash.FlashTimingType;
import org.sopt.makers.domain.crew.flash.service.FlashService;
import org.sopt.makers.domain.crew.meeting.Meeting;
import org.sopt.makers.domain.crew.meeting.MeetingCategory;
import org.sopt.makers.domain.crew.meeting.MeetingImage;
import org.sopt.makers.domain.crew.meeting.MeetingJoinablePart;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordType;
import org.sopt.makers.domain.crew.meeting.tag.MeetingTag;
import org.sopt.makers.domain.crew.meeting.tag.MeetingTagType;
import org.sopt.makers.domain.crew.meeting.tag.WelcomeMessageType;
import org.sopt.makers.domain.crew.meeting.tag.service.MeetingTagService;
import org.sopt.makers.domain.crew.notification.service.MeetingKeywordNotificationPublisher;

class FlashFacadeTest {

  private static final Clock CLOCK =
      Clock.fixed(Instant.parse("2026-08-24T03:00:00Z"), ZoneId.of("Asia/Seoul"));

  @Test
  @DisplayName("빈 이미지는 기본 이미지로 대체하고 번쩍과 태그를 생성한다")
  void createsFlashAndTagWithDefaultImage() {
    MeetingService meetingService = mock(MeetingService.class);
    FlashService flashService = mock(FlashService.class);
    MeetingTagService meetingTagService = mock(MeetingTagService.class);
    MeetingKeywordNotificationPublisher notificationPublisher =
        mock(MeetingKeywordNotificationPublisher.class);
    FlashFacade facade =
        new FlashFacade(
            meetingService, flashService, meetingTagService, notificationPublisher, CLOCK);

    Meeting meeting = meeting();
    Flash flash = flash();
    MeetingTag meetingTag =
        new MeetingTag(
            3L,
            MeetingTagType.FLASH,
            1L,
            2L,
            List.of(WelcomeMessageType.YB_WELCOME),
            List.of(MeetingKeywordType.EXERCISE),
            null,
            null);
    when(meetingService.createFlashMeeting(any(), eq(10L))).thenReturn(meeting);
    when(flashService.createFlash(eq(10L), eq(1L), any(), eq(36))).thenReturn(flash);
    when(meetingTagService.createFlashTag(any(), any(), any(), any())).thenReturn(meetingTag);

    FlashFacade.CreateFlashCommand command =
        new FlashFacade.CreateFlashCommand(
            new FlashFacade.FlashCommandValues(
                "러닝 번쩍",
                "같이 달려요",
                FlashTimingType.AFTER_DISCUSSION,
                LocalDateTime.of(2026, 8, 30, 0, 0),
                LocalDateTime.of(2026, 8, 30, 23, 59, 59),
                FlashPlaceType.OFFLINE,
                "잠실",
                1,
                5,
                List.of()),
            List.of(WelcomeMessageType.YB_WELCOME),
            List.of(MeetingKeywordType.EXERCISE));

    FlashFacade.CreatedFlash result = facade.createFlash(command, 10L);

    ArgumentCaptor<MeetingService.CreateFlashMeetingCommand> meetingCommandCaptor =
        ArgumentCaptor.forClass(MeetingService.CreateFlashMeetingCommand.class);
    verify(meetingService).createFlashMeeting(meetingCommandCaptor.capture(), eq(10L));
    assertThat(meetingCommandCaptor.getValue().images())
        .containsExactly(
            new MeetingImage(
                0, "https://makers-web-img.s3.ap-northeast-2.amazonaws.com/flash/flash.png"));
    verify(notificationPublisher).publish(meeting, List.of(MeetingKeywordType.EXERCISE));
    assertThat(result.meetingId()).isEqualTo(1L);
    assertThat(result.tagId()).isEqualTo(3L);
  }

  private Meeting meeting() {
    return new Meeting(
        1L,
        10L,
        null,
        "러닝 번쩍",
        null,
        MeetingCategory.FLASH,
        List.of(new MeetingImage(0, "https://example.com/default.png")),
        LocalDateTime.of(2026, 8, 24, 12, 0),
        LocalDateTime.of(2026, 8, 29, 23, 59, 59),
        5,
        "같이 달려요",
        "",
        LocalDateTime.of(2026, 8, 30, 0, 0),
        LocalDateTime.of(2026, 8, 30, 23, 59, 59),
        "",
        "",
        false,
        false,
        null,
        36,
        null,
        List.of(MeetingJoinablePart.values()),
        null,
        null);
  }

  private Flash flash() {
    return new Flash(
        2L,
        10L,
        1L,
        "러닝 번쩍",
        "같이 달려요",
        FlashTimingType.AFTER_DISCUSSION,
        LocalDateTime.of(2026, 8, 24, 12, 0),
        LocalDateTime.of(2026, 8, 29, 23, 59, 59),
        LocalDateTime.of(2026, 8, 30, 0, 0),
        LocalDateTime.of(2026, 8, 30, 23, 59, 59),
        FlashPlaceType.OFFLINE,
        "잠실",
        1,
        5,
        36,
        List.of(new MeetingImage(0, "https://example.com/default.png")),
        null,
        null);
  }
}
