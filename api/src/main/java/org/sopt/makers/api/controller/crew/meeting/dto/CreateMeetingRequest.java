package org.sopt.makers.api.controller.crew.meeting.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;

public record CreateMeetingRequest(
    Long meetingDemandId,
    @NotBlank(message = "모임 제목은 필수 입력 값입니다.") String title,
    @NotBlank(message = "모임 부제목은 필수 입력 값입니다.") @Size(max = 30) String subTitle,
    @NotEmpty(message = "모임 이미지는 필수 입력 값입니다.") @Size(max = 6)
        List<@NotBlank(message = "모임 이미지 URL은 필수 입력 값입니다.") String> files,
    @NotBlank(message = "모임 카테고리는 필수 입력 값입니다.") String category,
    @NotBlank(message = "모집 시작일은 필수 입력 값입니다.") String startDate,
    @NotBlank(message = "모집 종료일은 필수 입력 값입니다.") String endDate,
    @NotNull(message = "모집 인원은 필수 입력 값입니다.") Integer capacity,
    @NotBlank(message = "모임 설명은 필수 입력 값입니다.") String desc,
    String processDesc,
    String mStartDate,
    String mEndDate,
    String leaderDesc,
    String note,
    @NotNull(message = "멘토 필요 여부는 필수 입력 값입니다.") Boolean isMentorNeeded,
    @NotNull(message = "활동 기수 제한 여부는 필수 입력 값입니다.") Boolean canJoinOnlyActiveGeneration,
    @NotNull(message = "참여 정보는 필수 입력 값입니다.") @Valid MeetingJoinInfoRequest joinInfo,
    Integer createdGeneration,
    Integer targetActiveGeneration,
    @NotEmpty(message = "참여 가능 파트는 필수 입력 값입니다.") @Size(max = 6)
        List<@NotBlank(message = "참여 가능 파트는 필수 입력 값입니다.") String> joinableParts,
    List<@NotNull(message = "공동 모임장 userId는 필수 입력 값입니다.") Long> coLeaderUserIds) {

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
