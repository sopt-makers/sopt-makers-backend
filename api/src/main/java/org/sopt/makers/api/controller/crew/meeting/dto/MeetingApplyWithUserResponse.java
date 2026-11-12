package org.sopt.makers.api.controller.crew.meeting.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import org.sopt.makers.domain.crew.meeting.MeetingApply;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;

public record MeetingApplyWithUserResponse(
    Long id,
    int applyNumber,
    Integer type,
    Long meetingId,
    Long userId,
    String content,
    LocalDateTime appliedDate,
    Integer status,
    MeetingUserProfileResponse user) {

  /** 서비스가 정렬·필터링한 응답 목록 순서대로 1부터 번호를 부여한다. */
  public static List<MeetingApplyWithUserResponse> fromAll(
      List<MeetingService.ApplyDetail> details) {
    return IntStream.range(0, details.size())
        .mapToObj(index -> from(details.get(index), index + 1))
        .toList();
  }

  private static MeetingApplyWithUserResponse from(
      MeetingService.ApplyDetail detail, int applyNumber) {
    MeetingApply apply = detail.apply();
    return new MeetingApplyWithUserResponse(
        apply.id(),
        applyNumber,
        apply.type().getValue(),
        apply.meetingId(),
        apply.userId(),
        apply.content(),
        apply.appliedDate(),
        apply.status().getValue(),
        MeetingUserProfileResponse.from(detail.user()));
  }
}
