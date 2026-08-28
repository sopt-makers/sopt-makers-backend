package org.sopt.makers.api.controller.crew.advertisement;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.crew.advertisement.AdvertisementCategory;
import org.sopt.makers.domain.crew.advertisement.AdvertisementEventType;
import org.springframework.http.ResponseEntity;

@Tag(name = "CREW 광고", description = "CREW 광고 조회 API")
public interface AdvertisementApi {

  @Operation(summary = "게시글 또는 모임 광고 조회")
  ResponseEntity<BaseResponse<?>> getAdvertisements(AdvertisementCategory category);

  @Operation(summary = "모임 상단 광고 조회")
  ResponseEntity<BaseResponse<?>> getMeetingTopAdvertisement(
      AdvertisementEventType eventType, @Parameter(hidden = true) Long userId);
}
