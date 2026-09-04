package org.sopt.makers.api.controller.playground.report;

import static org.sopt.makers.api.controller.playground.report.ReportSuccessCode.GET_MY_REPORT_STATS;
import static org.sopt.makers.api.controller.playground.report.ReportSuccessCode.GET_REPORT_STATS;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.playground.report.dto.MySoptReportStatsResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.playground.report.SoptReportCategory;
import org.sopt.makers.domain.playground.report.service.SoptReportStatsQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report/stats")
public class ReportController implements ReportApi {

  private final SoptReportStatsQueryService soptReportStatsQueryService;

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getReportStats(
      @RequestParam(required = false, defaultValue = "SOPT") SoptReportCategory category) {
    return ResponseFactory.success(GET_REPORT_STATS, soptReportStatsQueryService.getSoptReportStats(category));
  }

  @Override
  @GetMapping("/me")
  public ResponseEntity<BaseResponse<?>> getMyReportStats(@CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_MY_REPORT_STATS,
        MySoptReportStatsResponse.from(soptReportStatsQueryService.getMySoptReportStats(userId)));
  }
}
