package org.sopt.makers.api.controller.app.schedule;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "앱 일정", description = "앱 일정 API")
public interface AppScheduleApi {

  @Operation(summary = "일정 리스트 조회")
  ResponseEntity<BaseResponse<?>> getSchedules(
      @Parameter(description = "조회 시작 일시") LocalDateTime start,
      @Parameter(description = "조회 종료 일시") LocalDateTime end);
}
