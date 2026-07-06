package org.sopt.makers.api.controller.crew.meeting.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;

public record UpdateMeetingRequest(
    String title,
    @Size(max = 30) String subTitle,
    @Size(max = 6) List<@NotBlank(message = "모임 이미지 URL은 필수 입력 값입니다.") String> files,
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
    @Valid MeetingJoinInfoRequest joinInfo,
    Integer targetActiveGeneration,
    @Size(max = 6) List<@NotBlank(message = "참여 가능 파트는 필수 입력 값입니다.") String> joinableParts,
    List<@NotNull(message = "공동 모임장 userId는 필수 입력 값입니다.") Long> coLeaderUserIds) {

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
