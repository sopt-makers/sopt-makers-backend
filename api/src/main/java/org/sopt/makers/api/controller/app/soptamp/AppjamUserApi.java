package org.sopt.makers.api.controller.app.soptamp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.app.soptamp.dto.AppjamUserResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "앱잼 유저", description = "앱잼 유저 API")
public interface AppjamUserApi {

  @Deprecated
  @Operation(summary = "앱잼 팀 정보 조회", description = "앱잼에 참여하지 않은 유저도 참여하지 않음 상태로 200을 준다.")
  @ApiResponse(responseCode = "200", description = "앱잼 팀 정보 조회에 성공했습니다.")
  ResponseEntity<BaseResponse<AppjamUserResponse.AppjamStatusResponse>> getTeamInfo(
      @Parameter(hidden = true) Long userId);
}
