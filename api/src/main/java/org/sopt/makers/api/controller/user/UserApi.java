package org.sopt.makers.api.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.user.dto.UserRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "유저", description = "유저 API")
public interface UserApi {

  @Operation(summary = "유저 프로필 수정")
  ResponseEntity<BaseResponse<?>> updateUserProfile(
      @Schema(hidden = true) Long userId, UserRequest.UserProfileInfo request);
}
