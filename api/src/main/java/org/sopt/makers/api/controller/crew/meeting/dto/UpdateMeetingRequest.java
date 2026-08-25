package org.sopt.makers.api.controller.crew.meeting.dto;

import jakarta.validation.constraints.Size;
import java.util.List;
import org.sopt.makers.domain.crew.meeting.facade.MeetingFacade;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;

public record UpdateMeetingRequest(
    String title,
    @Size(min = 1, max = 30) String subTitle,
    @Size(min = 1, max = 6) List<String> files,
    String category,
    String startDate,
    String endDate,
    Integer capacity,
    String desc,
    String processDesc,
    String mStartDate,
    String mEndDate,
    String leaderDesc,
    String note,
    Boolean isMentorNeeded,
    Boolean canJoinOnlyActiveGeneration,
    MeetingJoinInfoRequest joinInfo,
    Integer targetActiveGeneration,
    @Size(min = 1, max = 6) List<String> joinableParts,
    List<Long> coLeaderUserIds,
    List<String> welcomeMessageTypes,
    @Size(min = 1, max = 2) List<String> meetingKeywordTypes) {

  public MeetingFacade.UpdateMeetingCommand toCommand() {
    return new MeetingFacade.UpdateMeetingCommand(
        new MeetingService.UpdateMeetingCommand(
            title,
            subTitle,
            MeetingMapper.getCategory(category),
            MeetingMapper.getImageURL(files),
            MeetingMapper.getStartDate(startDate),
            MeetingMapper.getEndDate(endDate),
            capacity,
            desc,
            processDesc,
            MeetingMapper.getStartDate(mStartDate),
            MeetingMapper.getEndDate(mEndDate),
            leaderDesc,
            note,
            isMentorNeeded,
            canJoinOnlyActiveGeneration,
            MeetingMapper.getJoinInfo(joinInfo),
            targetActiveGeneration,
            MeetingMapper.getJoinableParts(joinableParts),
            coLeaderUserIds),
        MeetingTagMapper.toWelcomeMessageTypes(welcomeMessageTypes),
        MeetingTagMapper.toMeetingKeywordTypes(meetingKeywordTypes));
  }
}
