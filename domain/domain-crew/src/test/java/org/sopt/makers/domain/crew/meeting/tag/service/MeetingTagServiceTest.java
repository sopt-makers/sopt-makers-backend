package org.sopt.makers.domain.crew.meeting.tag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordType;
import org.sopt.makers.domain.crew.meeting.tag.MeetingTag;
import org.sopt.makers.domain.crew.meeting.tag.MeetingTagType;
import org.sopt.makers.domain.crew.meeting.tag.WelcomeMessageType;
import org.sopt.makers.domain.crew.meeting.tag.exception.MeetingTagException;
import org.sopt.makers.domain.crew.meeting.tag.port.MeetingTagRepositoryPort;

class MeetingTagServiceTest {

  private final InMemoryMeetingTagRepository repository = new InMemoryMeetingTagRepository();
  private final MeetingTagService meetingTagService = new MeetingTagService(repository);

  @Test
  @DisplayName("일반 모임 태그를 생성한다")
  void createsGeneralMeetingTag() {
    MeetingTag result =
        meetingTagService.createGeneralMeetingTag(
            1L, List.of(WelcomeMessageType.YB_WELCOME), List.of(MeetingKeywordType.EXERCISE));

    assertThat(result.type()).isEqualTo(MeetingTagType.MEETING);
    assertThat(result.meetingId()).isEqualTo(1L);
    assertThat(result.welcomeMessageTypes()).containsExactly(WelcomeMessageType.YB_WELCOME);
    assertThat(result.meetingKeywordTypes()).containsExactly(MeetingKeywordType.EXERCISE);
  }

  @Test
  @DisplayName("모임 키워드가 비어있으면 태그를 생성할 수 없다")
  void rejectsEmptyMeetingKeywords() {
    assertThatThrownBy(() -> meetingTagService.createGeneralMeetingTag(1L, List.of(), List.of()))
        .isInstanceOf(MeetingTagException.class);
  }

  @Test
  @DisplayName("번쩍 태그를 수정할 때 모임 키워드는 필수다")
  void requiresMeetingKeywordsWhenUpdatingFlashTag() {
    MeetingTag flashTag =
        meetingTagService.createFlashTag(
            2L, 1L, List.of(WelcomeMessageType.YB_WELCOME), List.of(MeetingKeywordType.EXERCISE));

    assertThatThrownBy(
            () ->
                meetingTagService.updateFlashTag(
                    flashTag.flashId(), List.of(WelcomeMessageType.OB_WELCOME), null))
        .isInstanceOf(MeetingTagException.class);
  }

  private static class InMemoryMeetingTagRepository implements MeetingTagRepositoryPort {

    private final List<MeetingTag> tags = new ArrayList<>();
    private long sequence = 1;

    @Override
    public MeetingTag save(MeetingTag tag) {
      MeetingTag saved =
          new MeetingTag(
              tag.id() == null ? sequence++ : tag.id(),
              tag.type(),
              tag.meetingId(),
              tag.flashId(),
              tag.welcomeMessageTypes(),
              tag.meetingKeywordTypes(),
              tag.createdAt(),
              tag.updatedAt());
      tags.removeIf(existing -> existing.id().equals(saved.id()));
      tags.add(saved);
      return saved;
    }

    @Override
    public Optional<MeetingTag> findByMeetingId(Long meetingId) {
      return tags.stream().filter(tag -> tag.meetingId().equals(meetingId)).findFirst();
    }

    @Override
    public Optional<MeetingTag> findByFlashId(Long flashId) {
      return tags.stream().filter(tag -> flashId.equals(tag.flashId())).findFirst();
    }

    @Override
    public List<MeetingTag> findAllByMeetingIds(List<Long> meetingIds) {
      return tags.stream().filter(tag -> meetingIds.contains(tag.meetingId())).toList();
    }

    @Override
    public void deleteByMeetingId(Long meetingId) {
      tags.removeIf(tag -> tag.meetingId().equals(meetingId));
    }

    @Override
    public void deleteByFlashId(Long flashId) {
      tags.removeIf(tag -> flashId.equals(tag.flashId()));
    }
  }
}
