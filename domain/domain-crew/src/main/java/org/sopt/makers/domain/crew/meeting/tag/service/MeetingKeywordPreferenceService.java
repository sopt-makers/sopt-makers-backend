package org.sopt.makers.domain.crew.meeting.tag.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordPreference;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordType;
import org.sopt.makers.domain.crew.meeting.tag.port.MeetingKeywordPreferenceRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingKeywordPreferenceService {

  private final MeetingKeywordPreferenceRepositoryPort preferenceRepositoryPort;

  @Transactional
  public MeetingKeywordPreference update(
      Long userId, List<MeetingKeywordType> meetingKeywordTypes) {
    List<MeetingKeywordType> safeKeywordTypes =
        meetingKeywordTypes == null ? List.of() : meetingKeywordTypes;
    MeetingKeywordPreference preference =
        preferenceRepositoryPort
            .findByUserId(userId)
            .orElseGet(() -> MeetingKeywordPreference.create(userId, List.of()));
    return preferenceRepositoryPort.save(preference.update(safeKeywordTypes));
  }

  public List<MeetingKeywordType> getByUserId(Long userId) {
    return preferenceRepositoryPort
        .findByUserId(userId)
        .map(MeetingKeywordPreference::keywordTypes)
        .orElseGet(List::of);
  }

  public List<Long> findInterestedUserIds(List<MeetingKeywordType> meetingKeywordTypes) {
    if (meetingKeywordTypes == null || meetingKeywordTypes.isEmpty()) {
      return List.of();
    }
    return preferenceRepositoryPort.findAll().stream()
        .filter(preference -> preference.isInterestedIn(meetingKeywordTypes))
        .map(MeetingKeywordPreference::userId)
        .toList();
  }
}
