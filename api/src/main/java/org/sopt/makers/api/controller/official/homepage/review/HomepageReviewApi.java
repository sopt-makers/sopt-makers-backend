package org.sopt.makers.api.controller.official.homepage.review;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.makers.api.controller.official.homepage.review.dto.HomepageReviewRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "메인 리뷰", description = "공식 홈페이지 메인 리뷰 API")
public interface HomepageReviewApi {

  @Operation(summary = "리뷰 생성")
  ResponseEntity<BaseResponse<?>> createReview(HomepageReviewRequest.Create request);

  @Operation(summary = "리뷰 목록 조회")
  ResponseEntity<BaseResponse<?>> getReviews();

  @Operation(summary = "리뷰 수정")
  ResponseEntity<BaseResponse<?>> editReview(Long id, HomepageReviewRequest.Edit request);
}
