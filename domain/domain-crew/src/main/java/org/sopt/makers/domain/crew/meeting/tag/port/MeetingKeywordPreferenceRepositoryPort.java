package org.sopt.makers.domain.crew.meeting.tag.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordPreference;

public interface MeetingKeywordPreferenceRepositoryPort {

  MeetingKeywordPreference save(MeetingKeywordPreference preference);

  Optional<MeetingKeywordPreference> findByUserId(Long userId);

  List<MeetingKeywordPreference> findAll();
}
