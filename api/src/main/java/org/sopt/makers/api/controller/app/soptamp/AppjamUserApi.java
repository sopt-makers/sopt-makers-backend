package org.sopt.makers.api.controller.app.soptamp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "앱잼 유저", description = "앱잼 유저 API")
public interface AppjamUserApi {

  @Deprecated
  @Operation(summary = "앱잼 팀 정보 조회")
  ResponseEntity<BaseResponse<?>> getTeamInfo(@Parameter(hidden = true) Long userId);
}
