package org.sopt.makers.domain.crew.meeting.tag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
  void 일반_모임_태그를_생성한다() {
    MeetingTag result =
        meetingTagService.createGeneralMeetingTag(
            1L, List.of(WelcomeMessageType.YB_WELCOME), List.of(MeetingKeywordType.EXERCISE));

    assertThat(result.type()).isEqualTo(MeetingTagType.MEETING);
    assertThat(result.meetingId()).isEqualTo(1L);
    assertThat(result.welcomeMessageTypes()).containsExactly(WelcomeMessageType.YB_WELCOME);
    assertThat(result.meetingKeywordTypes()).containsExactly(MeetingKeywordType.EXERCISE);
  }

  @Test
  void 모임_키워드가_비어있으면_태그를_생성할_수_없다() {
    assertThatThrownBy(() -> meetingTagService.createGeneralMeetingTag(1L, List.of(), List.of()))
        .isInstanceOf(MeetingTagException.class);
  }

  @Test
  void 번쩍_태그를_수정할_때_모임_키워드는_필수다() {
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
