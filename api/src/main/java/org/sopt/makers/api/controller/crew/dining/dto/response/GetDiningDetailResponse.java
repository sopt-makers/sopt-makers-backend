package org.sopt.makers.api.controller.crew.dining.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.sopt.makers.domain.crew.dining.model.enums.DiningPayerStatus;
import org.sopt.makers.domain.crew.dining.result.GetDiningDetailResult.MyAttendanceStatus;
import org.sopt.makers.domain.crew.meeting.MeetingJoinablePart;

@Schema(name = "GetDiningDetailResponse", description = "회식 상세 응답 Dto")
public record GetDiningDetailResponse(
    @Schema(description = "회식(모임) id", example = "1") @NotNull Long meetingId,
    @Schema(description = "회식명", example = "서버 파트 회식") @NotNull String title,
    @Schema(description = "회식 일자", example = "2026-10-30") @NotNull LocalDate diningDate,
    @Schema(description = "회식 일자 (수정됨) 표시 여부", example = "false") @NotNull boolean isDateModified,
    @Schema(description = "대표 이미지", example = "[url] 형식") @NotNull String imageUrl,
    @Schema(description = "종료 여부. false 면 진행 중", example = "false") @NotNull boolean isEnded,
    @Schema(description = "참여 파트", example = "[\"SERVER\", \"WEB\"]") @NotNull
        MeetingJoinablePart[] joinableParts,
    @Schema(description = "참여 파트 (수정됨) 표시 여부. 참여 파트 구성이 실제로 바뀐 경우에만 true", example = "false")
        @NotNull
        boolean isJoinablePartsModified,
    @Schema(description = "개설자") @NotNull DiningLeaderResponse leader,
    @Schema(description = "조회자가 개설자인지 여부", example = "false") @NotNull boolean isLeader,
    @Schema(
            description =
                "조회자가 회식 내 하나 이상의 회차에서 승인된 공동 결제자인지 여부. 회차 관리 권한은 isLeader 또는 해당 회차의 myPayerStatus가 APPROVED인지로 판단",
            example = "false")
        @NotNull
        boolean isApprovedPayer,
    @Schema(description = "회차 목록 (1차부터 순서대로)") @NotNull List<Round> rounds) {
  public static GetDiningDetailResponse from(
      org.sopt.makers.domain.crew.dining.result.GetDiningDetailResult value) {
    return new GetDiningDetailResponse(
        value.meetingId(),
        value.title(),
        value.diningDate(),
        value.isDateModified(),
        value.imageUrl(),
        value.isEnded(),
        value.joinableParts(),
        value.isJoinablePartsModified(),
        DiningLeaderResponse.from(value.leader()),
        value.isLeader(),
        value.isApprovedPayer(),
        value.rounds().stream().map(Round::from).toList());
  }

  @Schema(name = "DiningV2RoundDto", description = "회차")
  public record Round(
      @Schema(description = "회차 id", example = "3") @NotNull Long roundId,
      @Schema(description = "회차 번호 (1차 = 1)", example = "2") @NotNull int roundNumber,
      @Schema(description = "장소. null 이면 장소 미정", example = "비틀비틀 비틀스 타코 서순라길 점") String place,
      @Schema(description = "네이버지도 링크", example = "https://naver.me/abcdef") String naverMapLink,
      @Schema(description = "카카오맵 링크", example = "https://kko.to/abcdef") String kakaoMapLink,
      @Schema(description = "시작 시간 (HH:mm). null 이면 미노출", example = "19:45", type = "string")
          @JsonFormat(pattern = "HH:mm")
          LocalTime startTime,
      @Schema(description = "마감(확정) 여부", example = "false") @NotNull boolean isClosed,
      @Schema(description = "장소 (수정됨) 표시 여부", example = "false") @NotNull boolean isPlaceModified,
      @Schema(description = "장소 링크 (수정됨) 표시 여부", example = "false") @NotNull boolean isLinkModified,
      @Schema(description = "시작 시간 (수정됨) 표시 여부", example = "false") @NotNull
          boolean isStartTimeModified,
      @Schema(description = "NEW 표시 여부 (1차를 제외한 최신 회차이고 생성 24시간 이내)", example = "true") @NotNull
          boolean isNew,
      @Schema(description = "현재 참석 인원", example = "8") @NotNull long attendeeCount,
      @Schema(
              description = "내 참석 상태. NOT_ATTENDING: 미참석, ATTENDING: 참석, ATTENDED: 참석 완료(마감된 회차)",
              example = "ATTENDING")
          @NotNull
          MyAttendanceStatus myAttendanceStatus,
      @Schema(description = "내 공동 결제자 요청 상태. null 이면 요청 없음", example = "PENDING")
          DiningPayerStatus myPayerStatus,
      @Schema(description = "신규 회차 신청 안내 문구 노출 여부 (이전 회차에 참석했고 이 회차는 미신청)", example = "false")
          @NotNull
          boolean showNewRoundGuide) {
    public static Round from(
        org.sopt.makers.domain.crew.dining.result.GetDiningDetailResult.Round value) {
      return new Round(
          value.roundId(),
          value.roundNumber(),
          value.place(),
          value.naverMapLink(),
          value.kakaoMapLink(),
          value.startTime(),
          value.isClosed(),
          value.isPlaceModified(),
          value.isLinkModified(),
          value.isStartTimeModified(),
          value.isNew(),
          value.attendeeCount(),
          value.myAttendanceStatus(),
          value.myPayerStatus(),
          value.showNewRoundGuide());
    }
  }
}
