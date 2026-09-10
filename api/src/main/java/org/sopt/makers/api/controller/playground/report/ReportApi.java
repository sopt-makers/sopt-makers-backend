package org.sopt.makers.api.controller.playground.report;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.playground.report.SoptReportCategory;
import org.springframework.http.ResponseEntity;

@Tag(name = "솝트 리포트 API")
public interface ReportApi {

  @Operation(summary = "솝트 리포트 통계 조회")
  ResponseEntity<BaseResponse<?>> getReportStats(SoptReportCategory category);

  @Operation(summary = "나의 솝트 리포트 통계 조회")
  ResponseEntity<BaseResponse<?>> getMyReportStats(@Parameter(hidden = true) Long userId);
}
