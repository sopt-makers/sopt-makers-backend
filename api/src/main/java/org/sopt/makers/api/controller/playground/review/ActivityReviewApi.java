package org.sopt.makers.api.controller.playground.review;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.playground.review.dto.CreateActivityReviewRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "활동 후기 관련 API", description = "활동 후기 관련 API List")
@SecurityRequirement(name = "Authorization")
public interface ActivityReviewApi {

  @Operation(summary = "활동 후기 생성")
  ResponseEntity<BaseResponse<?>> createActivityReview(
      @Parameter(hidden = true) Long userId, CreateActivityReviewRequest request);

  @Operation(summary = "활동 후기 조회")
  ResponseEntity<BaseResponse<?>> getActivityReviews(int page, int size);
}
