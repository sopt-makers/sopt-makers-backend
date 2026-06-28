package org.sopt.makers.api.controller.app.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "앱 멤버", description = "앱 멤버 API")
public interface AppMemberApi {

  @Operation(summary = "전체 출석 정보 조회")
  ResponseEntity<BaseResponse<?>> getMemberTotalAttendance(@Parameter(hidden = true) Long userId);

  @Operation(summary = "출석 점수 조회")
  ResponseEntity<BaseResponse<?>> getScore(@Parameter(hidden = true) Long userId);
}
