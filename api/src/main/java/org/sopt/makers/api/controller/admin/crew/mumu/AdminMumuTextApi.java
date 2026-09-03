package org.sopt.makers.api.controller.admin.crew.mumu;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.admin.crew.mumu.dto.MumuTextUpsertRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "어드민 Playground 무무 텍스트", description = "무무 텍스트 관리 API")
public interface AdminMumuTextApi {

  @Operation(summary = "무무 텍스트 목록 조회")
  ResponseEntity<BaseResponse<?>> getMumuTexts();

  @Operation(summary = "무무 텍스트 생성")
  ResponseEntity<BaseResponse<?>> createMumuText(MumuTextUpsertRequest request);

  @Operation(summary = "무무 텍스트 수정")
  ResponseEntity<BaseResponse<?>> updateMumuText(Long mumuTextId, MumuTextUpsertRequest request);

  @Operation(summary = "무무 텍스트 삭제")
  ResponseEntity<BaseResponse<?>> deleteMumuText(Long mumuTextId);
}
