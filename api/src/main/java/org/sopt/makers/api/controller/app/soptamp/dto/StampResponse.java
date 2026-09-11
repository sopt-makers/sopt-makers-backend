package org.sopt.makers.api.controller.app.soptamp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.app.soptamp.facade.SoptampFacade;
import org.sopt.makers.domain.app.soptamp.stamp.Stamp;

public final class StampResponse {

  private StampResponse() {}

  public record StampMain(
      @Schema(description = "스탬프 아이디", example = "1") Long id,
      @Schema(description = "인증 내용", example = "팀원들과 함께 했어요") String contents,
      @Schema(description = "인증 이미지 주소 목록") List<String> images,
      @Schema(description = "활동 날짜", example = "2026-09-19") String activityDate,
      @Schema(description = "등록 일시", example = "2026-09-19T14:00:00") LocalDateTime createdAt,
      @Schema(description = "마지막 수정 일시", example = "2026-09-19T15:00:00") LocalDateTime updatedAt,
      @Schema(description = "인증한 미션 아이디", example = "1") Long missionId,
      @Schema(description = "받은 박수 수", example = "42") int clapCount,
      @Schema(description = "조회 수", example = "120") int viewCount) {

    public static StampMain of(Stamp stamp) {
      return new StampMain(
          stamp.id(),
          stamp.contents(),
          stamp.images(),
          stamp.activityDate(),
          stamp.createdAt(),
          stamp.updatedAt(),
          stamp.missionId(),
          stamp.clapCount(),
          stamp.viewCount());
    }
  }

  public record StampView(
      @Schema(description = "스탬프 아이디", example = "1") Long id,
      @Schema(description = "인증 내용", example = "팀원들과 함께 했어요") String contents,
      @Schema(description = "인증 이미지 주소 목록") List<String> images,
      @Schema(description = "활동 날짜", example = "2026-09-19") String activityDate,
      @Schema(description = "등록 일시", example = "2026-09-19T14:00:00") LocalDateTime createdAt,
      @Schema(description = "마지막 수정 일시", example = "2026-09-19T15:00:00") LocalDateTime updatedAt,
      @Schema(description = "인증한 미션 아이디", example = "1") Long missionId,
      @Schema(description = "작성자의 솝탬프 닉네임", example = "김앱짱") String ownerNickname,
      @Schema(description = "받은 박수 수", example = "42") int clapCount,
      @Schema(description = "조회 수", example = "120") int viewCount,
      @Schema(description = "내가 쓴 스탬프인지 여부", example = "true") @JsonProperty("isMine")
          boolean isMine,
      @Schema(description = "내가 이 스탬프에 보낸 박수 수", example = "5") int myClapCount) {

    public static StampView of(SoptampFacade.StampView view) {
      Stamp stamp = view.stamp();
      return new StampView(
          stamp.id(),
          stamp.contents(),
          stamp.images(),
          stamp.activityDate(),
          stamp.createdAt(),
          stamp.updatedAt(),
          stamp.missionId(),
          view.ownerNickname(),
          stamp.clapCount(),
          stamp.viewCount(),
          view.mine(),
          view.myClapCount());
    }
  }

  public record StampId(@Schema(description = "스탬프 아이디", example = "1") Long stampId) {

    public static StampId of(Stamp stamp) {
      return new StampId(stamp.id());
    }
  }

  public record SoptampReportResponse(
      @Schema(description = "솝탬프 신고 폼 주소", example = "https://forms.gle/example")
          String reportUrl) {

    public static SoptampReportResponse of(SoptampFacade.SoptampReport report) {
      return new SoptampReportResponse(report.reportUrl());
    }
  }
}
