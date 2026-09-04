package org.sopt.makers.clients.crew.dto;

import java.util.List;

public record CrewFastestAppliedGroupHttpResponse(List<CrewGroupHttpDto> topFastestAppliedMeetings) {

  public record CrewGroupHttpDto(Long meetingId, String title) {}
}
