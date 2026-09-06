package org.sopt.makers.api.controller.playground.popup;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.playground.popup.dto.PopupRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "팝업 관련 API", description = "웹 팝업 CRUD API")
public interface PopupApi {

  @Operation(summary = "팝업 생성", description = "admin-key 헤더 필수")
  ResponseEntity<BaseResponse<?>> createPopup(String adminKey, PopupRequest request);

  @Operation(summary = "전체 팝업 조회", description = "admin-key 헤더 필수")
  ResponseEntity<BaseResponse<?>> getAllPopups(String adminKey);

  @Operation(summary = "팝업 단건 조회", description = "admin-key 헤더 필수")
  ResponseEntity<BaseResponse<?>> getPopupById(String adminKey, Long id);

  @Operation(summary = "팝업 수정", description = "admin-key 헤더 필수")
  ResponseEntity<BaseResponse<?>> updatePopup(String adminKey, Long id, PopupRequest request);

  @Operation(summary = "팝업 삭제", description = "admin-key 헤더 필수")
  ResponseEntity<BaseResponse<?>> deletePopup(String adminKey, Long id);

  @Operation(summary = "Admin Key 검증")
  ResponseEntity<BaseResponse<?>> validateAdminKey(String adminKey);

  @Operation(summary = "현재 운영 중인 팝업 조회", description = "현재 날짜 기준으로 시작일이 가장 빠른 팝업 반환. 없으면 null")
  @SecurityRequirement(name = "Authorization")
  ResponseEntity<BaseResponse<?>> getCurrentPopup();
}
