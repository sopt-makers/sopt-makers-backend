package org.sopt.makers.api.controller.crew.dining.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "관리자용 회차 참석자 목록")
public record GetAttendeeManagementResponse(
    @NotNull List<Attendee> attendees, @NotNull DiningPageMetaResponse meta) {
  public static GetAttendeeManagementResponse from(
      org.sopt.makers.domain.crew.dining.result.GetAttendeeManagementResult value) {
    return new GetAttendeeManagementResponse(
        value.attendees().stream().map(Attendee::from).toList(),
        DiningPageMetaResponse.from(value.meta()));
  }

  @Schema(name = "DiningV2ManagementAttendeeDto", description = "관리자용 참석자")
  public record Attendee(
      @Schema(description = "크루 사용자 ID") @NotNull Long id,
      @Schema(description = "현재 참석자의 등록 시각 기준 번호 (오래된 순서부터 1번)") @NotNull long attendanceNumber,
      @NotNull String name,
      String profileImage,
      @Schema(description = "가장 최근 활동 기수") Integer recentGeneration,
      String phone) {
    public static Attendee from(
        org.sopt.makers.domain.crew.dining.result.GetAttendeeManagementResult.Attendee value) {
      return new Attendee(
          value.id(),
          value.attendanceNumber(),
          value.name(),
          value.profileImage(),
          value.recentGeneration(),
          value.phone());
    }
  }
}
