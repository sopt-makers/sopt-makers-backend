package org.sopt.makers.api.controller.crew.meeting.dto;

import jakarta.validation.constraints.Size;
import java.util.List;
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
    List<Long> coLeaderUserIds) {

  public MeetingService.UpdateMeetingCommand toCommand() {
    return new MeetingService.UpdateMeetingCommand(
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
        coLeaderUserIds);
  }
}
