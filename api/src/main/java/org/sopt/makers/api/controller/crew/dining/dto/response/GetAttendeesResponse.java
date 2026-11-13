package org.sopt.makers.api.controller.crew.dining.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "로그인 사용자용 회차 참석 현황 모달 목록")
public record GetAttendeesResponse(@NotNull List<Attendee> attendees) {
  public static GetAttendeesResponse from(
      org.sopt.makers.domain.crew.dining.result.GetAttendeesResult value) {
    return new GetAttendeesResponse(value.attendees().stream().map(Attendee::from).toList());
  }

  @Schema(name = "DiningV2AttendeeDto", description = "참석 현황 모달 참석자")
  public record Attendee(@NotNull Long id, @NotNull String name, String profileImage) {
    public static Attendee from(
        org.sopt.makers.domain.crew.dining.result.GetAttendeesResult.Attendee value) {
      return new Attendee(value.id(), value.name(), value.profileImage());
    }
  }
}
