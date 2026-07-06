package org.sopt.makers.api.controller.crew.meeting.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.crew.meeting.Meeting;
import org.sopt.makers.domain.crew.meeting.MeetingImage;
import org.sopt.makers.domain.crew.meeting.MeetingJoinInfo;
import org.sopt.makers.domain.crew.meeting.MeetingJoinablePart;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;

public record MeetingDetailResponse(
    Long id,
    Long userId,
    Long meetingDemandId,
    String title,
    String subTitle,
    String category,
    List<MeetingImage> imageURL,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Integer capacity,
    String desc,
    String processDesc,
    LocalDateTime mStartDate,
    LocalDateTime mEndDate,
    String leaderDesc,
    String note,
    Boolean isMentorNeeded,
    Boolean canJoinOnlyActiveGeneration,
    MeetingJoinInfo joinInfo,
    Integer createdGeneration,
    Integer targetActiveGeneration,
    List<MeetingJoinablePart> joinableParts,
    MeetingUserResponse meetingCreator,
    List<MeetingUserResponse> coMeetingLeaders,
    Boolean isHost,
    Boolean apply,
    Boolean approved,
    Boolean isCoLeader,
    long approvedApplyCount,
    List<MeetingApplyWithUserResponse> applies) {

  public static MeetingDetailResponse from(Meeting meeting) {
    return new MeetingDetailResponse(
        meeting.id(),
        meeting.userId(),
        meeting.meetingDemandId(),
        meeting.title(),
        meeting.subTitle(),
        meeting.category().getValue(),
        meeting.images(),
        meeting.startDate(),
        meeting.endDate(),
        meeting.capacity(),
        meeting.description(),
        meeting.processDescription(),
        meeting.activityStartDate(),
        meeting.activityEndDate(),
        meeting.leaderDescription(),
        meeting.note(),
        meeting.isMentorNeeded(),
        meeting.canJoinOnlyActiveGeneration(),
        meeting.joinInfo(),
        meeting.createdGeneration(),
        meeting.targetActiveGeneration(),
        meeting.joinableParts(),
        null,
        List.of(),
        null,
        null,
        null,
        null,
        0,
        List.of());
  }

  public static MeetingDetailResponse from(MeetingService.MeetingDetail detail) {
    Meeting meeting = detail.meeting();
    return new MeetingDetailResponse(
        meeting.id(),
        meeting.userId(),
        meeting.meetingDemandId(),
        meeting.title(),
        meeting.subTitle(),
        meeting.category().getValue(),
        meeting.images(),
        meeting.startDate(),
        meeting.endDate(),
        meeting.capacity(),
        meeting.description(),
        meeting.processDescription(),
        meeting.activityStartDate(),
        meeting.activityEndDate(),
        meeting.leaderDescription(),
        meeting.note(),
        meeting.isMentorNeeded(),
        meeting.canJoinOnlyActiveGeneration(),
        meeting.joinInfo(),
        meeting.createdGeneration(),
        meeting.targetActiveGeneration(),
        meeting.joinableParts(),
        MeetingUserResponse.from(detail.leader()),
        detail.coLeaders().stream().map(MeetingUserResponse::from).toList(),
        detail.isHost(),
        detail.isApply(),
        detail.isApproved(),
        detail.isCoLeader(),
        detail.approvedApplyCount(),
        detail.applies().stream().map(MeetingApplyWithUserResponse::from).toList());
  }
}
