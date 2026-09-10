package org.sopt.makers.api.controller.playground.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Makers 만든 사람들 관련 API")
public interface MakersUserApi {

  @Operation(summary = "메이커스 만든 사람들을 위한 전체 조회 API")
  ResponseEntity<BaseResponse<?>> getMakersProfiles();
}
