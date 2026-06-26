package org.sopt.makers.api.controller.official.review;

import static org.sopt.makers.api.controller.official.review.ReviewSuccessCode.CREATE_REVIEW;
import static org.sopt.makers.api.controller.official.review.ReviewSuccessCode.GET_RANDOM_REVIEWS;
import static org.sopt.makers.api.controller.official.review.ReviewSuccessCode.GET_REVIEWS;
import static org.sopt.makers.api.controller.official.review.ReviewSuccessCode.GET_REVIEWS_BY_AUTHOR;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.official.review.dto.ReviewRequest;
import org.sopt.makers.api.controller.official.review.dto.ReviewResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.official.review.Review;
import org.sopt.makers.domain.official.review.ReviewAuthorReviews;
import org.sopt.makers.domain.official.review.ReviewSearchCondition;
import org.sopt.makers.domain.official.review.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "활동후기", description = "공식 홈페이지 활동후기 API")
@RestController
@RequestMapping("/api/v1/official/reviews")
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;

  @PostMapping
  @Operation(summary = "활동후기 추가")
  public ResponseEntity<BaseResponse<?>> createReview(
      @Valid @RequestBody ReviewRequest.Create request) {
    Long reviewId = reviewService.create(request.toCommand());
    return ResponseFactory.success(CREATE_REVIEW, new ReviewResponse.Create(reviewId));
  }

  @GetMapping
  @Operation(summary = "활동후기 목록 조회")
  public ResponseEntity<BaseResponse<?>> getReviews(
      @ParameterObject @Valid @ModelAttribute ReviewRequest.Search request) {
    ReviewSearchCondition condition = request.toCondition();
    List<Review> reviews =
        reviewService.search(condition, request.offset(), request.limitOrDefault());
    long totalCount = reviewService.count(condition);

    return ResponseFactory.success(
        GET_REVIEWS,
        ReviewResponse.Page.of(
            ReviewResponse.of(reviews),
            (int) totalCount,
            request.limitOrDefault(),
            request.pageNoOrDefault()));
  }

  @GetMapping("/random")
  @Operation(summary = "랜덤 활동후기 파트별로 하나씩 조회")
  public ResponseEntity<BaseResponse<?>> getRandomReviewsByPart() {
    return ResponseFactory.success(
        GET_RANDOM_REVIEWS, ReviewResponse.of(reviewService.getRandomReviewsByPart()));
  }

  @GetMapping("/internal")
  @Operation(summary = "Playground Internal - 유저 활동후기 데이터 조회")
  public ResponseEntity<BaseResponse<?>> getReviewsByAuthor(
      @ParameterObject @Valid @ModelAttribute ReviewRequest.Author request) {
    ReviewAuthorReviews result = reviewService.getReviewsByAuthor(request.name());
    return ResponseFactory.success(GET_REVIEWS_BY_AUTHOR, ReviewResponse.AuthorReviews.of(result));
  }
}
