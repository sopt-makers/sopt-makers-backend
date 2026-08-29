package org.sopt.makers.api.controller.crew.property;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "CREW 프로퍼티", description = "CREW 프로퍼티 조회 API")
public interface CrewPropertyApi {

  @Operation(summary = "프로퍼티 단건 또는 전체 조회")
  ResponseEntity<BaseResponse<?>> getProperty(String key);

  @Operation(summary = "홈 컨텐츠 프로퍼티 조회")
  ResponseEntity<BaseResponse<?>> getHomeProperty();
}
