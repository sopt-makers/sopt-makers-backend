package org.sopt.makers.api.controller.app.attendance;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.app.attendance.dto.AttendRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "앱 출석", description = "앱 출석 API")
public interface AppAttendanceApi {

  @Operation(summary = "출석 체크")
  ResponseEntity<BaseResponse<?>> attend(
      @Parameter(hidden = true) Long userId, AttendRequest request);
}
