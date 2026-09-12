package org.sopt.makers.api.controller.app.firebase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.app.firebase.dto.FirebaseResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "firebase", description = "앱 firebase 연동 정보 API")
public interface FirebaseApi {

  @Operation(summary = "firebase 연동을 위한 정보 조회")
  @ApiResponse(responseCode = "200", description = "firebase 정보 조회에 성공했습니다.")
  ResponseEntity<BaseResponse<FirebaseResponse>> getFirebaseInfo();
}
