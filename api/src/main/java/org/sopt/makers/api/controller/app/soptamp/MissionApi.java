package org.sopt.makers.api.controller.app.soptamp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.sopt.makers.api.controller.app.soptamp.dto.MissionRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "솝탬프 미션", description = "앱 솝탬프 미션 API")
public interface MissionApi {

  @Operation(summary = "전체 미션 조회", description = "완료 여부를 함께 준다.")
  ResponseEntity<BaseResponse<?>> getAllMissions(@Parameter(hidden = true) Long userId);

  @Operation(summary = "[SERVER 편의 메서드] 미션 생성")
  ResponseEntity<BaseResponse<?>> registerMission(
      @Valid MissionRequest.RegisterMissionRequest request);

  @Operation(summary = "완료한 미션 조회")
  ResponseEntity<BaseResponse<?>> getCompletedMissions(@Parameter(hidden = true) Long userId);

  @Operation(summary = "미완료 미션 조회")
  ResponseEntity<BaseResponse<?>> getIncompleteMissions(@Parameter(hidden = true) Long userId);
}
