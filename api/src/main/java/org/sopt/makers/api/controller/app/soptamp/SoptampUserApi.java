package org.sopt.makers.api.controller.app.soptamp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.app.soptamp.dto.EditProfileMessageRequest;
import org.sopt.makers.api.controller.app.soptamp.dto.ProfileMessageResponse;
import org.sopt.makers.api.controller.app.soptamp.dto.SoptampUserResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "솝탬프 유저", description = "앱 솝탬프 유저 API")
public interface SoptampUserApi {

  @Operation(summary = "솝탬프 정보 조회")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "솝탬프 정보 조회에 성공했습니다."),
    @ApiResponse(responseCode = "404", description = "존재하지 않는 유저입니다.", content = @Content)
  })
  ResponseEntity<BaseResponse<SoptampUserResponse>> getSoptampUser(
      @Parameter(hidden = true) Long userId);

  @Operation(summary = "한마디 변경")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "한마디 변경에 성공했습니다."),
    @ApiResponse(responseCode = "404", description = "존재하지 않는 유저입니다.", content = @Content)
  })
  ResponseEntity<BaseResponse<ProfileMessageResponse>> editProfileMessage(
      @Parameter(hidden = true) Long userId, EditProfileMessageRequest request);
}
