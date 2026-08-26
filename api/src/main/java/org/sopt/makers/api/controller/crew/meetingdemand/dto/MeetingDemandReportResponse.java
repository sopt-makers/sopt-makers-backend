package org.sopt.makers.api.controller.crew.meetingdemand.dto;

import org.sopt.makers.domain.crew.meeting.demand.MeetingDemandReport;

public record MeetingDemandReportResponse(Long reportId) {

  public static MeetingDemandReportResponse from(MeetingDemandReport report) {
    return new MeetingDemandReportResponse(report.id());
  }
}
