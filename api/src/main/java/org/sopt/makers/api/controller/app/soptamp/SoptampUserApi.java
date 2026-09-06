package org.sopt.makers.api.controller.app.soptamp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.app.soptamp.dto.EditProfileMessageRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "솝탬프 유저", description = "앱 솝탬프 유저 API")
public interface SoptampUserApi {

  @Operation(summary = "솝탬프 정보 조회")
  ResponseEntity<BaseResponse<?>> getSoptampUser(@Parameter(hidden = true) Long userId);

  @Operation(summary = "한마디 변경")
  ResponseEntity<BaseResponse<?>> editProfileMessage(
      @Parameter(hidden = true) Long userId, EditProfileMessageRequest request);
}
