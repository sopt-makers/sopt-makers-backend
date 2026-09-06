package org.sopt.makers.domain.crew.meeting.port;

import java.util.List;

/** 기존 {@code GET /internal/meetings/related-user-ids/:userId}를 대체하는 내부 Port. */
public interface PlaygroundRelatedMeetingUserPort {

  RelatedMeetingUsers findRelatedUserIds(Long userId);

  record RelatedMeetingUsers(
      List<Long> currentGenerationUserIds, List<Long> pastGenerationUserIds) {

    public RelatedMeetingUsers {
      currentGenerationUserIds =
          currentGenerationUserIds == null ? List.of() : List.copyOf(currentGenerationUserIds);
      pastGenerationUserIds =
          pastGenerationUserIds == null ? List.of() : List.copyOf(pastGenerationUserIds);
    }
  }
}
