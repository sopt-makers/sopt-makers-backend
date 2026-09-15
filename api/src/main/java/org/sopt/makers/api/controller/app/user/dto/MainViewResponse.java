package org.sopt.makers.api.controller.app.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.sopt.makers.domain.app.home.MainView;

public record MainViewResponse(
    @Schema(description = "플레이그라운드 프로필 정보. 비로그인이면 빈 값으로 채워진다") Playground user,
    @Schema(description = "출석 관련 정보. 현재는 항상 고정값") Operation operation,
    @Schema(description = "인앱 알림을 모두 읽었는지 여부", example = "false") @JsonProperty("isAllConfirm")
        boolean isAllConfirm) {

  private static final Double DEFAULT_ATTENDANCE_SCORE = 0D;
  private static final String DEFAULT_ANNOUNCEMENT = "";

  public static MainViewResponse of(MainView mainView) {
    return new MainViewResponse(
        new Playground(
            mainView.status().name(),
            mainView.name(),
            mainView.profileImage(),
            mainView.part(),
            mainView.generationList()),
        new Operation(DEFAULT_ATTENDANCE_SCORE, DEFAULT_ANNOUNCEMENT),
        mainView.isAllConfirm());
  }

  public record Playground(
      @Schema(description = "활동 상태. 비로그인이면 UNAUTHENTICATED", example = "ACTIVE") String status,
      @Schema(description = "유저 이름. 비로그인이면 빈 문자열", example = "김앱짱") String name,
      @Schema(description = "프로필 이미지 주소. 비로그인이면 빈 문자열", example = "https://s3.sopt.org/profile.png")
          String profileImage,
      @Schema(description = "SOPT 활동 파트. 비로그인이면 빈 문자열", example = "서버") String part,
      @Schema(description = "활동한 기수 목록. 비로그인이면 빈 배열") List<Long> generationList) {}

  public record Operation(
      @Schema(description = "출석 점수. 현재는 항상 0", example = "0.0") Double attendanceScore,
      @Schema(description = "공지 문구. 현재는 항상 빈 문자열", example = "") String announcement) {}
}
