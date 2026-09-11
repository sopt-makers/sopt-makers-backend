package org.sopt.makers.api.controller.app.soptletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReportFormResponse(
    @Schema(description = "익명 신고 폼 주소", example = "https://forms.gle/example")
        String reportFormUrl) {

  public static ReportFormResponse of(String reportFormUrl) {
    return new ReportFormResponse(reportFormUrl);
  }
}
