package org.sopt.makers.domain.crew.meeting.tag.service;

import static org.sopt.makers.domain.crew.meeting.tag.exception.MeetingTagFailure.INVALID_MEETING_KEYWORD_SIZE;
import static org.sopt.makers.domain.crew.meeting.tag.exception.MeetingTagFailure.INVALID_TAG_VALUE;
import static org.sopt.makers.domain.crew.meeting.tag.exception.MeetingTagFailure.NOT_FOUND_TAG;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordType;
import org.sopt.makers.domain.crew.meeting.tag.MeetingTag;
import org.sopt.makers.domain.crew.meeting.tag.WelcomeMessageType;
import org.sopt.makers.domain.crew.meeting.tag.exception.MeetingTagException;
import org.sopt.makers.domain.crew.meeting.tag.port.MeetingTagRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingTagService {

  private static final int MAX_MEETING_KEYWORD_SIZE = 2;

  private final MeetingTagRepositoryPort meetingTagRepositoryPort;

  @Transactional
  public MeetingTag createGeneralMeetingTag(
      Long meetingId,
      List<WelcomeMessageType> welcomeMessageTypes,
      List<MeetingKeywordType> meetingKeywordTypes) {
    validateMeetingId(meetingId);
    validateMeetingKeywordTypes(meetingKeywordTypes);
    return meetingTagRepositoryPort.save(
        MeetingTag.createGeneralMeetingTag(meetingId, welcomeMessageTypes, meetingKeywordTypes));
  }

  @Transactional
  public MeetingTag createFlashTag(
      Long flashId,
      Long meetingId,
      List<WelcomeMessageType> welcomeMessageTypes,
      List<MeetingKeywordType> meetingKeywordTypes) {
    validateMeetingId(meetingId);
    if (flashId == null) {
      throw new MeetingTagException(INVALID_TAG_VALUE);
    }
    validateMeetingKeywordTypes(meetingKeywordTypes);
    return meetingTagRepositoryPort.save(
        MeetingTag.createFlashTag(flashId, meetingId, welcomeMessageTypes, meetingKeywordTypes));
  }

  @Transactional
  public MeetingTag updateGeneralMeetingTag(
      Long meetingId,
      List<WelcomeMessageType> welcomeMessageTypes,
      List<MeetingKeywordType> meetingKeywordTypes) {
    validateMeetingId(meetingId);
    validateNullableMeetingKeywordTypes(meetingKeywordTypes);
    MeetingTag tag =
        meetingTagRepositoryPort
            .findByMeetingId(meetingId)
            .orElseGet(
                () -> {
                  validateMeetingKeywordTypes(meetingKeywordTypes);
                  return MeetingTag.createGeneralMeetingTag(
                      meetingId,
                      welcomeMessageTypes == null ? List.of() : welcomeMessageTypes,
                      meetingKeywordTypes);
                });
    return meetingTagRepositoryPort.save(tag.update(welcomeMessageTypes, meetingKeywordTypes));
  }

  @Transactional
  public MeetingTag updateFlashTag(
      Long flashId,
      List<WelcomeMessageType> welcomeMessageTypes,
      List<MeetingKeywordType> meetingKeywordTypes) {
    validateMeetingKeywordTypes(meetingKeywordTypes);
    MeetingTag tag =
        meetingTagRepositoryPort
            .findByFlashId(flashId)
            .orElseThrow(() -> new MeetingTagException(NOT_FOUND_TAG));
    return meetingTagRepositoryPort.save(tag.update(welcomeMessageTypes, meetingKeywordTypes));
  }

  public MeetingTagInfo getByMeetingId(Long meetingId) {
    return meetingTagRepositoryPort
        .findByMeetingId(meetingId)
        .map(MeetingTagInfo::from)
        .orElseGet(MeetingTagInfo::empty);
  }

  public MeetingTagInfo getByFlashId(Long flashId) {
    return meetingTagRepositoryPort
        .findByFlashId(flashId)
        .map(MeetingTagInfo::from)
        .orElseGet(MeetingTagInfo::empty);
  }

  public Map<Long, MeetingTagInfo> getByMeetingIds(List<Long> meetingIds) {
    if (meetingIds == null || meetingIds.isEmpty()) {
      return Map.of();
    }
    return meetingTagRepositoryPort.findAllByMeetingIds(meetingIds).stream()
        .collect(
            Collectors.toMap(
                MeetingTag::meetingId, MeetingTagInfo::from, (existing, ignored) -> existing));
  }

  @Transactional
  public void deleteByMeetingId(Long meetingId) {
    meetingTagRepositoryPort.deleteByMeetingId(meetingId);
  }

  @Transactional
  public void deleteByFlashId(Long flashId) {
    meetingTagRepositoryPort.deleteByFlashId(flashId);
  }

  private void validateMeetingId(Long meetingId) {
    if (meetingId == null) {
      throw new MeetingTagException(INVALID_TAG_VALUE);
    }
  }

  private void validateMeetingKeywordTypes(List<MeetingKeywordType> meetingKeywordTypes) {
    if (meetingKeywordTypes == null
        || meetingKeywordTypes.isEmpty()
        || meetingKeywordTypes.size() > MAX_MEETING_KEYWORD_SIZE) {
      throw new MeetingTagException(INVALID_MEETING_KEYWORD_SIZE);
    }
  }

  private void validateNullableMeetingKeywordTypes(List<MeetingKeywordType> meetingKeywordTypes) {
    if (meetingKeywordTypes != null) {
      validateMeetingKeywordTypes(meetingKeywordTypes);
    }
  }

  public record MeetingTagInfo(
      Long tagId,
      List<WelcomeMessageType> welcomeMessageTypes,
      List<MeetingKeywordType> meetingKeywordTypes) {

    public MeetingTagInfo {
      welcomeMessageTypes =
          welcomeMessageTypes == null ? List.of() : List.copyOf(welcomeMessageTypes);
      meetingKeywordTypes =
          meetingKeywordTypes == null ? List.of() : List.copyOf(meetingKeywordTypes);
    }

    public static MeetingTagInfo from(MeetingTag meetingTag) {
      return new MeetingTagInfo(
          meetingTag.id(), meetingTag.welcomeMessageTypes(), meetingTag.meetingKeywordTypes());
    }

    public static MeetingTagInfo empty() {
      return new MeetingTagInfo(null, List.of(), List.of());
    }
  }
}
