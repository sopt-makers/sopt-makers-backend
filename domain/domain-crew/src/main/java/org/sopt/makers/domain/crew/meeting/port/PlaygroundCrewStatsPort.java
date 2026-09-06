package org.sopt.makers.domain.crew.meeting.port;

import java.util.List;

/** 기존 {@code GET /internal/meeting/stats/fastest-applied/:orgId}를 대체하는 내부 Port. */
public interface PlaygroundCrewStatsPort {

  List<FastestAppliedMeeting> findFastestAppliedMeetings(
      Long userId, int queryCount, int queryYear);

  record FastestAppliedMeeting(Long meetingId, String title) {}
}
