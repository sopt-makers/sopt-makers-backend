package org.sopt.makers.domain.crew.advertisement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.crew.advertisement.Advertisement;
import org.sopt.makers.domain.crew.advertisement.AdvertisementCategory;
import org.sopt.makers.domain.crew.advertisement.AdvertisementEventType;
import org.sopt.makers.domain.crew.advertisement.AdvertisementTargetGeneration;
import org.sopt.makers.domain.crew.advertisement.MeetingTopAdvertisement;
import org.sopt.makers.domain.crew.advertisement.exception.AdvertisementException;
import org.sopt.makers.domain.crew.advertisement.port.AdvertisementActiveGenerationPort;
import org.sopt.makers.domain.crew.advertisement.port.AdvertisementImageStoragePort;
import org.sopt.makers.domain.crew.advertisement.port.AdvertisementRepositoryPort;
import org.sopt.makers.domain.crew.meeting.MeetingUser;
import org.sopt.makers.domain.crew.meeting.port.MeetingRepositoryPort;
import org.sopt.makers.domain.crew.meeting.port.MeetingUserPort;
import org.sopt.makers.domain.user.Activity;

class AdvertisementServiceTest {

  private static final Clock CLOCK =
      Clock.fixed(Instant.parse("2026-08-28T03:00:00Z"), ZoneId.of("Asia/Seoul"));

  private final AdvertisementRepositoryPort repository = mock(AdvertisementRepositoryPort.class);
  private final AdvertisementActiveGenerationPort generationPort =
      mock(AdvertisementActiveGenerationPort.class);
  private final AdvertisementImageStoragePort imageStoragePort =
      mock(AdvertisementImageStoragePort.class);
  private final MeetingRepositoryPort meetingRepositoryPort = mock(MeetingRepositoryPort.class);
  private final MeetingUserPort meetingUserPort = mock(MeetingUserPort.class);
  private final AdvertisementService service =
      new AdvertisementService(
          repository,
          generationPort,
          imageStoragePort,
          meetingRepositoryPort,
          meetingUserPort,
          CLOCK);

  @Test
  @DisplayName("기간 내 후원 광고가 없으면 기본 광고를 조회한다")
  void fallsBackToDefaultAdvertisements() {
    Advertisement fallback = advertisement(1, AdvertisementCategory.POST, null, false);
    when(repository.findSponsoredInPeriod(AdvertisementCategory.POST, LocalDateTime.now(CLOCK), 6))
        .thenReturn(List.of());
    when(repository.findDefault(AdvertisementCategory.POST, 6)).thenReturn(List.of(fallback));

    assertThat(service.getGeneralAdvertisements(AdvertisementCategory.POST))
        .containsExactly(fallback);
  }

  @Test
  @DisplayName("현재 기수 기획 사용자에게 솝커톤 신청 모임을 연결한다")
  void connectsSopkathonApplicationMeeting() {
    Advertisement advertisement =
        advertisement(2, AdvertisementCategory.MEETING_TOP, AdvertisementEventType.SOPKATHON, true);
    when(repository.findDisplayedMeetingTop(LocalDateTime.now(CLOCK)))
        .thenReturn(List.of(advertisement));
    when(generationPort.getActiveGeneration()).thenReturn(39);
    when(meetingUserPort.findById(10L))
        .thenReturn(
            Optional.of(
                new MeetingUser(
                    10L, "사용자", null, List.of(Activity.of(39, null, Part.PLAN, true)))));
    when(meetingRepositoryPort.findFirstIdByTitle("[39기 솝커톤] 기획 파트 신청"))
        .thenReturn(Optional.of(30L));

    MeetingTopAdvertisement result =
        service.getMeetingTopAdvertisement(10L, AdvertisementEventType.SOPKATHON).orElseThrow();

    assertThat(result.activeGeneration()).isEqualTo(39);
    assertThat(result.applicationMeetingId()).isEqualTo(30L);
  }

  @Test
  @DisplayName("다른 모임 상단 광고가 노출 중이면 추가 노출을 거부한다")
  void rejectsDuplicateDisplayedMeetingTop() {
    Advertisement advertisement =
        advertisement(
            2, AdvertisementCategory.MEETING_TOP, AdvertisementEventType.NETWORKING, false);
    when(repository.findById(2)).thenReturn(Optional.of(advertisement));
    when(repository.existsOtherDisplayed(AdvertisementCategory.MEETING_TOP, 2)).thenReturn(true);
    Advertisement.UpdateValues values =
        new Advertisement.UpdateValues(true, null, null, null, null, null, null, null, null, null);

    assertThatThrownBy(() -> service.updateMeetingTopAdvertisement(2, values))
        .isInstanceOf(AdvertisementException.class);
    verify(repository).lockAllByCategory(AdvertisementCategory.MEETING_TOP);
  }

  @Test
  @DisplayName("모임 상단 이미지는 날짜별 경로에 업로드한다")
  void uploadsMeetingTopImageToDatedDirectory() {
    AdvertisementImageStoragePort.UploadImage image =
        new AdvertisementImageStoragePort.UploadImage(
            "banner.png", "image/png", 3, new ByteArrayInputStream(new byte[] {1, 2, 3}));
    when(imageStoragePort.upload(any(), any())).thenReturn("https://image");

    assertThat(service.uploadMeetingTopImage(image)).isEqualTo("https://image");

    ArgumentCaptor<String> directory = ArgumentCaptor.forClass(String.class);
    verify(imageStoragePort).upload(any(), directory.capture());
    assertThat(directory.getValue()).isEqualTo("meeting_top/2026/08/28");
  }

  private Advertisement advertisement(
      int id, AdvertisementCategory category, AdvertisementEventType eventType, boolean display) {
    return new Advertisement(
        id,
        "https://desktop",
        "https://mobile",
        "https://link",
        "https://calendar",
        "prefix",
        "highlight",
        "suffix",
        "subtitle",
        category,
        1L,
        LocalDateTime.of(2026, 8, 1, 0, 0),
        LocalDateTime.of(2026, 8, 31, 23, 59),
        false,
        display,
        eventType,
        AdvertisementTargetGeneration.ALL,
        null,
        null);
  }
}
