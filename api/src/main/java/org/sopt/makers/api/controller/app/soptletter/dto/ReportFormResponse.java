package org.sopt.makers.api.controller.app.soptletter.dto;

public record ReportFormResponse(String reportFormUrl) {

  public static ReportFormResponse of(String reportFormUrl) {
    return new ReportFormResponse(reportFormUrl);
  }
}
