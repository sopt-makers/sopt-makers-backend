package org.sopt.makers.api.controller.app.calendar;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "앱 일정", description = "현재 기수 일정 API")
public interface CalendarApi {

  @Operation(summary = "현재 기수 전체 일정 조회")
  ResponseEntity<BaseResponse<?>> getAllCalendar();

  @Operation(summary = "다가오는 일정 조회", description = "다가오는 일정이 없으면 마지막 일정을 준다")
  ResponseEntity<BaseResponse<?>> getRecentCalendar();
}
