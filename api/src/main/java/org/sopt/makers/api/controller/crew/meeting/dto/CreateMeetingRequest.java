package org.sopt.makers.api.controller.crew.meeting.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;

public record CreateMeetingRequest(
    Long meetingDemandId,
    @NotNull String title,
    @NotNull @Size(min = 1, max = 30) String subTitle,
    @NotEmpty @Size(min = 1, max = 6) List<String> files,
    @NotNull String category,
    @NotNull String startDate,
    @NotNull String endDate,
    @NotNull Integer capacity,
    @NotNull String desc,
    String processDesc,
    String mStartDate,
    String mEndDate,
    String leaderDesc,
    String note,
    @NotNull Boolean isMentorNeeded,
    @NotNull Boolean canJoinOnlyActiveGeneration,
    @NotNull MeetingJoinInfoRequest joinInfo,
    Integer createdGeneration,
    Integer targetActiveGeneration,
    @NotNull @Size(min = 1, max = 6) List<String> joinableParts,
    List<Long> coLeaderUserIds) {

  public MeetingService.CreateMeetingCommand toCommand() {
    return new MeetingService.CreateMeetingCommand(
        meetingDemandId,
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
        createdGeneration,
        targetActiveGeneration,
        MeetingMapper.getJoinableParts(joinableParts),
        coLeaderUserIds);
  }
}
