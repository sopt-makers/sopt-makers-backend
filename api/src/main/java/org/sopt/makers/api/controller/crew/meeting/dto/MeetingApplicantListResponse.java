package org.sopt.makers.api.controller.crew.meeting.dto;

import java.util.List;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;

public record MeetingApplicantListResponse(List<MeetingApplyWithUserResponse> applicants) {

  public MeetingApplicantListResponse {
    applicants = applicants == null ? List.of() : List.copyOf(applicants);
  }

  public static MeetingApplicantListResponse from(List<MeetingService.ApplyDetail> applicants) {
    return new MeetingApplicantListResponse(
        applicants.stream().map(MeetingApplyWithUserResponse::from).toList());
  }
}
