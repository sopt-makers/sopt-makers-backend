package org.sopt.makers.api.controller.playground.resolution;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.playground.resolution.dto.LuckyPickResponse;
import org.sopt.makers.api.controller.playground.resolution.dto.ResolutionResponse;
import org.sopt.makers.api.controller.playground.resolution.dto.ResolutionSaveRequest;
import org.sopt.makers.api.controller.playground.resolution.dto.ResolutionValidResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "다짐 메시지 관련 API", description = "다짐 메세지 관련 API List")
@SecurityRequirement(name = "Authorization")
public interface UserResolutionApi {

    @Operation(summary = "다짐 메세지 조회")
    ResponseEntity<BaseResponse<?>> getResolution(@Parameter(hidden = true) Long userId);

    @Operation(summary = "다짐 메세지 생성")
    ResponseEntity<BaseResponse<?>> createResolution(@Parameter(hidden = true) Long userId, ResolutionSaveRequest request);

    @Operation(summary = "다짐 메세지 유효성 검사")
    ResponseEntity<BaseResponse<?>> validation(@Parameter(hidden = true) Long userId);

    @Operation(summary = "다짐 메세지 삭제")
    ResponseEntity<BaseResponse<?>> deleteResolution(@Parameter(hidden = true) Long userId);

    @Operation(summary = "행운 뽑기 결과 조회")
    ResponseEntity<BaseResponse<?>> getLuckyPickResult(@Parameter(hidden = true) Long userId);
}
