package org.sopt.makers.api.controller.crew.flash.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.api.controller.crew.meeting.dto.MeetingApplyWithUserResponse;
import org.sopt.makers.api.controller.crew.meeting.dto.MeetingTagMapper;
import org.sopt.makers.api.controller.crew.meeting.dto.MeetingUserResponse;
import org.sopt.makers.domain.crew.flash.Flash;
import org.sopt.makers.domain.crew.flash.facade.FlashFacade;
import org.sopt.makers.domain.crew.meeting.MeetingCategory;
import org.sopt.makers.domain.crew.meeting.MeetingImage;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;

public record FlashDetailResponse(
    Long id,
    Long leaderUserId,
    String title,
    String category,
    List<MeetingImage> imageURL,
    LocalDateTime endDate,
    Integer minimumCapacity,
    Integer maximumCapacity,
    List<String> welcomeMessageTypes,
    List<String> meetingKeywordTypes,
    String desc,
    LocalDateTime activityStartDate,
    LocalDateTime activityEndDate,
    String flashTimingType,
    String flashPlaceType,
    String flashPlace,
    Integer createdGeneration,
    int status,
    long approvedApplyCount,
    boolean host,
    boolean apply,
    boolean approved,
    MeetingUserResponse user,
    List<MeetingApplyWithUserResponse> appliedInfo) {

  public static FlashDetailResponse from(FlashFacade.FlashDetail result) {
    Flash flash = result.flash();
    MeetingService.MeetingDetail meeting = result.meetingDetail();
    return new FlashDetailResponse(
        flash.meetingId(),
        flash.leaderUserId(),
        flash.title(),
        MeetingCategory.FLASH.getValue(),
        flash.images(),
        flash.endDate(),
        flash.minimumCapacity(),
        flash.maximumCapacity(),
        MeetingTagMapper.fromWelcomeMessageTypes(result.meetingTagInfo().welcomeMessageTypes()),
        MeetingTagMapper.fromMeetingKeywordTypes(result.meetingTagInfo().meetingKeywordTypes()),
        flash.description(),
        flash.activityStartDate(),
        flash.activityEndDate(),
        flash.timingType().getValue(),
        flash.placeType().getValue(),
        flash.place(),
        flash.createdGeneration(),
        result.status().getValue(),
        meeting.approvedApplyCount(),
        meeting.isHost(),
        meeting.isApply(),
        meeting.isApproved(),
        MeetingUserResponse.from(meeting.leader()),
        meeting.applies().stream().map(MeetingApplyWithUserResponse::from).toList());
  }
}
