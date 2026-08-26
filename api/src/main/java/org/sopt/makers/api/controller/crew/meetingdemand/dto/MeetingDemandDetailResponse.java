package org.sopt.makers.api.controller.crew.meetingdemand.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.crew.meeting.demand.MeetingDemand;
import org.sopt.makers.domain.crew.meeting.demand.service.MeetingDemandService;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordType;

public record MeetingDemandDetailResponse(
    Long id,
    String shortIntro,
    String expectation,
    String status,
    Boolean isMine,
    String anonymousNickname,
    String anonymousImageUrl,
    int openedMeetingCount,
    List<String> meetingKeywordTypes,
    MeetingDemandJoinInfoResponse joinInfo,
    int waitCount,
    Boolean isWaiting,
    int commentCount,
    LocalDateTime createdDate) {

  public static MeetingDemandDetailResponse from(MeetingDemandService.MeetingDemandDetail detail) {
    MeetingDemand demand = detail.meetingDemand();
    return new MeetingDemandDetailResponse(
        demand.id(),
        demand.shortIntro(),
        demand.expectation(),
        demand.status().name(),
        detail.isMine(),
        demand.anonymousNickname(),
        demand.anonymousImageUrl(),
        detail.openedMeetingCount(),
        demand.meetingKeywordTypes().stream().map(MeetingKeywordType::getValue).toList(),
        MeetingDemandJoinInfoResponse.from(demand.joinInfo()),
        demand.waitCount(),
        detail.isWaiting(),
        demand.commentCount(),
        demand.createdAt());
  }
}
