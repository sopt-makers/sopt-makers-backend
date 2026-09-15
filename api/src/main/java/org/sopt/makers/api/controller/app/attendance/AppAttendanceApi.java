package org.sopt.makers.api.controller.app.attendance;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.app.attendance.dto.AttendRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "앱 출석", description = "앱 출석 API")
public interface AppAttendanceApi {

  @Operation(summary = "출석 체크")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "출석 체크가 완료되었습니다"),
    @ApiResponse(
        responseCode = "400",
        description = "출석 체크가 아직 시작되지 않았거나, 시간이 종료되었거나, 출석 코드가 올바르지 않습니다",
        content = @Content),
    @ApiResponse(
        responseCode = "404",
        description = "존재하지 않는 세부 세션이거나, 해당 세션의 출석 정보를 찾을 수 없습니다",
        content = @Content)
  })
  ResponseEntity<BaseResponse<Void>> attend(
      @Parameter(hidden = true) Long userId, AttendRequest request);
}
