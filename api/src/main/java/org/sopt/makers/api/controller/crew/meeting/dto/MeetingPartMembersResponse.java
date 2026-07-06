package org.sopt.makers.api.controller.crew.meeting.dto;

import java.util.List;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;

public record MeetingPartMembersResponse(
    String part,
    int participantCount,
    boolean isActiveGeneration,
    Integer activeGeneration,
    List<MeetingApplyWithUserResponse> appliedInfo) {

  public static MeetingPartMembersResponse from(MeetingService.MeetingPartMembers members) {
    return new MeetingPartMembersResponse(
        members.part(),
        members.participantCount(),
        members.isActiveGeneration(),
        members.activeGeneration(),
        members.appliedInfo().stream().map(MeetingApplyWithUserResponse::from).toList());
  }
}
