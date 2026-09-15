package org.sopt.makers.api.controller.app.schedule;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import org.sopt.makers.api.controller.app.schedule.dto.ScheduleListResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "앱 일정", description = "앱 일정 API")
public interface AppScheduleApi {

  @Operation(summary = "일정 리스트 조회")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "일정 리스트 조회가 완료되었습니다"),
    @ApiResponse(
        responseCode = "400",
        description = "조회 기간이 1일 미만이거나 31일을 넘었습니다",
        content = @Content)
  })
  ResponseEntity<BaseResponse<ScheduleListResponse>> getSchedules(
      @Parameter(description = "조회 시작 일시", example = "2026-09-01T00:00:00") LocalDateTime start,
      @Parameter(description = "조회 종료 일시", example = "2026-09-30T23:59:59") LocalDateTime end);
}
