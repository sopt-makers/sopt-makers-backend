package org.sopt.makers.api.controller.app.calendar;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.sopt.makers.api.controller.app.calendar.dto.CalendarResponse;
import org.sopt.makers.api.controller.app.calendar.dto.RecentCalendarResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "앱 일정", description = "현재 기수 일정 API")
public interface CalendarApi {

  @Operation(summary = "현재 기수 전체 일정 조회")
  @ApiResponse(responseCode = "200", description = "전체 일정 조회에 성공했습니다.")
  ResponseEntity<BaseResponse<List<CalendarResponse>>> getAllCalendar();

  @Operation(summary = "다가오는 일정 조회", description = "다가오는 일정이 없으면 마지막 일정을 준다")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "다가오는 일정 조회에 성공했습니다."),
    @ApiResponse(responseCode = "404", description = "일정이 없습니다.", content = @Content)
  })
  ResponseEntity<BaseResponse<RecentCalendarResponse>> getRecentCalendar();
}
