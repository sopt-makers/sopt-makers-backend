package org.sopt.makers.api.controller.admin.attendance;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.admin.attendance.dto.SubAttendanceUpdateRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "어드민 출석", description = "어드민 출석 API")
public interface AdminAttendanceApi {

  @Operation(summary = "유저별 출석 조회")
  ResponseEntity<BaseResponse<?>> getAttendancesByUser(Long userId);

  @Operation(summary = "서브 출석 수정")
  ResponseEntity<BaseResponse<?>> updateSubAttendance(SubAttendanceUpdateRequest request);

  @Operation(summary = "출석 점수 수정")
  ResponseEntity<BaseResponse<?>> updateAttendanceScore(Long userId, int generation);
}
