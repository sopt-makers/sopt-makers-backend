package org.sopt.makers.api.controller.crew.meetingdemand.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.sopt.makers.domain.crew.meeting.demand.service.MeetingDemandService;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordType;

public record CreateMeetingDemandRequest(
    @NotBlank @Size(max = 30) String shortIntro,
    @NotBlank @Size(max = 1000) String expectation,
    @NotEmpty @Size(max = 2) List<String> meetingKeywordTypes,
    MeetingDemandJoinInfoRequest joinInfo) {

  public MeetingDemandService.CreateMeetingDemandCommand toCommand() {
    return new MeetingDemandService.CreateMeetingDemandCommand(
        shortIntro,
        expectation,
        meetingKeywordTypes.stream().map(MeetingKeywordType::ofValue).toList(),
        joinInfo == null ? null : joinInfo.toDomainOrNull());
  }
}
