package org.sopt.makers.api.controller.crew.soptmap;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.crew.soptmap.dto.GetSoptMapsRequest;
import org.sopt.makers.api.controller.crew.soptmap.dto.SoptMapBodyRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "CREW 솝맵", description = "CREW 솝맵 API")
public interface SoptMapApi {

  @Operation(summary = "솝맵 장소 등록")
  ResponseEntity<BaseResponse<?>> create(
      SoptMapBodyRequest request, @Parameter(hidden = true) Long userId);

  @Operation(summary = "솝맵 장소 수정")
  ResponseEntity<BaseResponse<?>> update(
      Long soptMapId, SoptMapBodyRequest request, @Parameter(hidden = true) Long userId);

  @Operation(summary = "솝맵 장소 삭제")
  ResponseEntity<BaseResponse<?>> delete(Long soptMapId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "지하철역 검색")
  ResponseEntity<BaseResponse<?>> searchSubwayStations(String keyword);

  @Operation(summary = "솝맵 목록 조회")
  ResponseEntity<BaseResponse<?>> getSoptMaps(
      GetSoptMapsRequest request, @Parameter(hidden = true) Long userId);

  @Operation(summary = "솝맵 상세 조회")
  ResponseEntity<BaseResponse<?>> getSoptMap(Long soptMapId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "솝맵 추천 토글")
  ResponseEntity<BaseResponse<?>> toggleRecommend(
      Long soptMapId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "솝맵 이벤트 당첨 여부 확인")
  ResponseEntity<BaseResponse<?>> checkEvent(Long soptMapId, @Parameter(hidden = true) Long userId);

  @Operation(summary = "솝맵 이벤트 선물 조회")
  ResponseEntity<BaseResponse<?>> getGift(Long soptMapId, @Parameter(hidden = true) Long userId);
}
