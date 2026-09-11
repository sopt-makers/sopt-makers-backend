package org.sopt.makers.api.controller.app.home.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record HomeDescriptionResponse(
    @Schema(
            description = "홈 상단에 띄우는 활동 기간 문구. 이름을 b 태그로 감싼 HTML이 섞여 있다",
            example = "<b>김앱짱</b>님은<br>SOPT와 12개월째")
        String activityDescription) {

  public static HomeDescriptionResponse of(String activityDescription) {
    return new HomeDescriptionResponse(activityDescription);
  }
}
