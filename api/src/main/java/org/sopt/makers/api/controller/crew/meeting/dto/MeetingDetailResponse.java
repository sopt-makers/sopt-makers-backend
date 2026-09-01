package org.sopt.makers.api.controller.crew.meeting.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.crew.meeting.Meeting;
import org.sopt.makers.domain.crew.meeting.MeetingImage;
import org.sopt.makers.domain.crew.meeting.MeetingJoinInfo;
import org.sopt.makers.domain.crew.meeting.MeetingJoinablePart;
import org.sopt.makers.domain.crew.meeting.facade.MeetingFacade;
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
    List<MeetingApplyWithUserResponse> applies,
    List<String> welcomeMessageTypes,
    List<String> meetingKeywordTypes) {

  public static MeetingDetailResponse from(MeetingFacade.MeetingDetailResult result) {
    MeetingService.MeetingDetail detail = result.meetingDetail();
    Meeting meeting = detail.meeting();
    return baseFrom(
        meeting,
        MeetingUserResponse.from(detail.leader()),
        detail.coLeaders().stream().map(MeetingUserResponse::from).toList(),
        detail.isHost(),
        detail.isApply(),
        detail.isApproved(),
        detail.isCoLeader(),
        detail.approvedApplyCount(),
        detail.applies().stream().map(MeetingApplyWithUserResponse::from).toList(),
        MeetingTagMapper.fromWelcomeMessageTypes(result.meetingTagInfo().welcomeMessageTypes()),
        MeetingTagMapper.fromMeetingKeywordTypes(result.meetingTagInfo().meetingKeywordTypes()));
  }

  private static MeetingDetailResponse baseFrom(
      Meeting meeting,
      MeetingUserResponse meetingCreator,
      List<MeetingUserResponse> coMeetingLeaders,
      Boolean isHost,
      Boolean apply,
      Boolean approved,
      Boolean isCoLeader,
      long approvedApplyCount,
      List<MeetingApplyWithUserResponse> applies,
      List<String> welcomeMessageTypes,
      List<String> meetingKeywordTypes) {
    return new MeetingDetailResponse(
        meeting.id(),
        meetingCreator.userId(),
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
        meetingCreator,
        coMeetingLeaders,
        isHost,
        apply,
        approved,
        isCoLeader,
        approvedApplyCount,
        applies,
        welcomeMessageTypes,
        meetingKeywordTypes);
  }
}
