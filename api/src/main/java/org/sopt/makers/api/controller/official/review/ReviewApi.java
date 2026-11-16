package org.sopt.makers.api.controller.official.review;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.official.review.dto.ReviewRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;

@Tag(name = "활동후기", description = "공식 홈페이지 활동후기 API")
public interface ReviewApi {

  @Operation(summary = "활동후기 추가")
  ResponseEntity<BaseResponse<?>> createReview(ReviewRequest.Create request, String apiKey);

  @Operation(summary = "활동후기 목록 조회")
  ResponseEntity<BaseResponse<?>> getReviews(@ParameterObject ReviewRequest.Search request);

  @Operation(summary = "랜덤 활동후기 파트별로 하나씩 조회")
  ResponseEntity<BaseResponse<?>> getRandomReviewsByPart();
}
