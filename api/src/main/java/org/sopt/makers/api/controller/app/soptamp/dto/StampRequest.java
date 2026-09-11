package org.sopt.makers.api.controller.app.soptamp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public final class StampRequest {

  private StampRequest() {}

  public record FindStampRequest(
      @Schema(description = "조회할 미션 아이디", example = "1")
          @NotNull(message = "missionId may not be null")
          Long missionId,
      @Schema(description = "조회할 유저의 솝탬프 닉네임", example = "김앱짱")
          @NotNull(message = "nickname may not be null")
          String nickname) {}

  public record RegisterStampRequest(
      @Schema(description = "인증할 미션 아이디", example = "1")
          @NotNull(message = "missionId may not be null")
          Long missionId,
      @Schema(description = "인증 이미지 주소", example = "https://s3.sopt.org/stamp/uuid.png")
          @NotNull(message = "image may not be null")
          String image,
      @Schema(description = "인증 내용", example = "팀원들과 함께 했어요")
          @NotNull(message = "contents may not be null")
          @NotEmpty(message = "contents may not be empty")
          String contents,
      @Schema(description = "활동 날짜", example = "2026-09-19")
          @NotNull(message = "activity date may not be null")
          String activityDate) {}

  public record EditStampRequest(
      @Schema(description = "인증할 미션 아이디", example = "1")
          @NotNull(message = "missionId may not be null")
          Long missionId,
      @Schema(description = "인증 이미지 주소", example = "https://s3.sopt.org/stamp/uuid.png")
          @NotNull(message = "image may not be null")
          String image,
      @Schema(description = "인증 내용", example = "팀원들과 함께 했어요")
          @NotNull(message = "contents may not be null")
          @NotEmpty(message = "contents may not be empty")
          String contents,
      @Schema(description = "활동 날짜", example = "2026-09-19")
          @NotNull(message = "activity date may not be null")
          String activityDate) {}
}
