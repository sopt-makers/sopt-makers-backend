package org.sopt.makers.api.controller.crew.meeting.dto;

import java.util.List;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;

public record MeetingParticipantListResponse(List<MeetingParticipantResponse> participants) {

  public MeetingParticipantListResponse {
    participants = participants == null ? List.of() : List.copyOf(participants);
  }

  public static MeetingParticipantListResponse from(
      List<MeetingService.MemberDetail> participants) {
    return new MeetingParticipantListResponse(
        participants.stream().map(MeetingParticipantResponse::from).toList());
  }

  public record MeetingParticipantResponse(String role, MeetingUserResponse user) {

    public static MeetingParticipantResponse from(MeetingService.MemberDetail detail) {
      return new MeetingParticipantResponse(
          detail.member().role().name(), MeetingUserResponse.from(detail.user()));
    }
  }
}
