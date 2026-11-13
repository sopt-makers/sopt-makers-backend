package org.sopt.makers.api.controller.crew.dining.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import org.sopt.makers.domain.crew.meeting.MeetingJoinablePart;

@Schema(name = "GetDiningsResponse", description = "회식 메인 목록 응답 Dto")
public record GetDiningsResponse(
    @NotNull List<DiningCard> dinings, @NotNull DiningPageMetaResponse meta) {
  public static GetDiningsResponse from(
      org.sopt.makers.domain.crew.dining.result.GetDiningsResult value) {
    return new GetDiningsResponse(
        value.dinings().stream().map(DiningCard::from).toList(),
        DiningPageMetaResponse.from(value.meta()));
  }

  @Schema(name = "DiningV2DiningCardDto", description = "회식 카드")
  public record DiningCard(
      @Schema(description = "회식(모임) id", example = "1") @NotNull Long meetingId,
      @Schema(description = "회식명", example = "서버 파트 회식") @NotNull String title,
      @Schema(description = "회식 일자", example = "2026-10-30") @NotNull LocalDate diningDate,
      @Schema(description = "대표 이미지", example = "[url] 형식") @NotNull String imageUrl,
      @Schema(description = "종료 여부. false 면 진행 중", example = "false") @NotNull boolean isEnded,
      @Schema(description = "참여 파트", example = "[\"SERVER\", \"WEB\"]") @NotNull
          MeetingJoinablePart[] joinableParts,
      @Schema(description = "전체 회차의 고유 참석자 수", example = "12") @NotNull long attendeeCount,
      @Schema(description = "개설자") @NotNull DiningLeaderResponse leader,
      @Schema(description = "참석자 관리 버튼 노출 여부 (개설자 또는 승인된 공동 결제자)", example = "true") @NotNull
          boolean canManageAttendees) {
    public static DiningCard from(
        org.sopt.makers.domain.crew.dining.result.GetDiningsResult.DiningCard value) {
      return new DiningCard(
          value.meetingId(),
          value.title(),
          value.diningDate(),
          value.imageUrl(),
          value.isEnded(),
          value.joinableParts(),
          value.attendeeCount(),
          DiningLeaderResponse.from(value.leader()),
          value.canManageAttendees());
    }
  }
}
