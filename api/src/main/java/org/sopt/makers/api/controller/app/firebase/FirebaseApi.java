package org.sopt.makers.api.controller.app.firebase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "firebase", description = "앱 firebase 연동 정보 API")
public interface FirebaseApi {

  @Operation(summary = "firebase 연동을 위한 정보 조회")
  ResponseEntity<BaseResponse<?>> getFirebaseInfo();
}
