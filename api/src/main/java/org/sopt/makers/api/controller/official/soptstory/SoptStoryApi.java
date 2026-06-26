package org.sopt.makers.api.controller.official.soptstory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.sopt.makers.api.controller.official.soptstory.dto.CreateSoptStoryRequest;
import org.sopt.makers.api.controller.official.soptstory.dto.GetSoptStoryListRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;

@Tag(name = "솝트스토리", description = "공식 홈페이지 솝트스토리 API")
public interface SoptStoryApi {

  @Operation(summary = "솝트스토리 생성", description = "솝트스토리를 생성합니다.")
  ResponseEntity<BaseResponse<?>> createSoptStory(CreateSoptStoryRequest request);

  @Operation(summary = "솝트스토리 좋아요 누르기")
  ResponseEntity<BaseResponse<?>> likeSoptStory(Long id, HttpServletRequest request);

  @Operation(summary = "솝트스토리 좋아요 취소하기")
  ResponseEntity<BaseResponse<?>> unlikeSoptStory(Long id, HttpServletRequest request);

  @Operation(summary = "솝트스토리 리스트 조회")
  ResponseEntity<BaseResponse<?>> getSoptStoryList(
      @ParameterObject GetSoptStoryListRequest request, HttpServletRequest httpRequest);
}
