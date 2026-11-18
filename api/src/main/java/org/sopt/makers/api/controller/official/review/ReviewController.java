package org.sopt.makers.api.controller.official.review;

import static org.sopt.makers.api.controller.official.review.ReviewSuccessCode.CREATE_REVIEW;
import static org.sopt.makers.api.controller.official.review.ReviewSuccessCode.GET_RANDOM_REVIEWS;
import static org.sopt.makers.api.controller.official.review.ReviewSuccessCode.GET_REVIEWS;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.official.common.dto.OfficialPageResponse;
import org.sopt.makers.api.controller.official.review.dto.ReviewRequest;
import org.sopt.makers.api.controller.official.review.dto.ReviewResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.auth.exception.AuthException;
import org.sopt.makers.domain.auth.exception.AuthFailure;
import org.sopt.makers.domain.official.review.Review;
import org.sopt.makers.domain.official.review.ReviewSearchCondition;
import org.sopt.makers.domain.official.review.service.ReviewService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/reviews")
@RequiredArgsConstructor
public class ReviewController implements ReviewApi {

  private final ReviewService reviewService;

  @Value("${official.apikey:}")
  private String officialApiKey;

  @Override
  @PostMapping
  public ResponseEntity<BaseResponse<?>> createReview(
      @Valid @RequestBody ReviewRequest.Create request,
      @RequestHeader(value = "api-key", required = false) String apiKey) {
    validateOfficialApiKey(apiKey);
    reviewService.create(request.toCommand());
    return ResponseFactory.success(CREATE_REVIEW, "Success");
  }

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getReviews(
      @Valid @ModelAttribute ReviewRequest.Search request) {
    ReviewSearchCondition condition = request.toCondition();
    List<Review> reviews =
        reviewService.search(condition, request.offset(), request.limitOrDefault());
    long totalCount = reviewService.count(condition);

    return ResponseFactory.success(
        GET_REVIEWS,
        OfficialPageResponse.of(
            ReviewResponse.of(reviews),
            (int) totalCount,
            request.limitOrDefault(),
            request.pageNoOrDefault()));
  }

  @Override
  @GetMapping("/random")
  public ResponseEntity<BaseResponse<?>> getRandomReviewsByPart() {
    return ResponseFactory.success(
        GET_RANDOM_REVIEWS, ReviewResponse.of(reviewService.getRandomReviewsByPart()));
  }

  private void validateOfficialApiKey(String apiKey) {
    if (apiKey == null || apiKey.isBlank() || !apiKey.equals(officialApiKey)) {
      throw new AuthException(AuthFailure.INVALID_API_KEY);
    }
  }
}
