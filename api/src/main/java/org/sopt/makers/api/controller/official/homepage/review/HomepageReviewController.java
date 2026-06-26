package org.sopt.makers.api.controller.official.homepage.review;

import static org.sopt.makers.api.controller.official.homepage.review.HomepageReviewSuccessCode.CREATE_HOMEPAGE_REVIEW;
import static org.sopt.makers.api.controller.official.homepage.review.HomepageReviewSuccessCode.GET_HOMEPAGE_REVIEWS;
import static org.sopt.makers.api.controller.official.homepage.review.HomepageReviewSuccessCode.UPDATE_HOMEPAGE_REVIEW;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.official.homepage.review.dto.HomepageReviewRequest;
import org.sopt.makers.api.controller.official.homepage.review.dto.HomepageReviewResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.official.homepage.review.HomepageReview;
import org.sopt.makers.domain.official.homepage.review.service.HomepageReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/official/homepage-reviews")
@RequiredArgsConstructor
@Validated
public class HomepageReviewController implements HomepageReviewApi {

  private final HomepageReviewService homepageReviewService;

  @Override
  @PostMapping
  public ResponseEntity<BaseResponse<?>> createReview(
      @Valid @RequestBody HomepageReviewRequest.Create request) {
    HomepageReview result = homepageReviewService.create(request.toCommand());
    return ResponseFactory.success(CREATE_HOMEPAGE_REVIEW, HomepageReviewResponse.of(result));
  }

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getReviews() {
    List<HomepageReview> result = homepageReviewService.getReviews();
    return ResponseFactory.success(GET_HOMEPAGE_REVIEWS, HomepageReviewResponse.of(result));
  }

  @Override
  @PatchMapping("/{id}")
  public ResponseEntity<BaseResponse<?>> editReview(
      @PathVariable Long id, @Valid @RequestBody HomepageReviewRequest.Edit request) {
    HomepageReview result = homepageReviewService.edit(id, request.toCommand());
    return ResponseFactory.success(UPDATE_HOMEPAGE_REVIEW, HomepageReviewResponse.of(result));
  }
}
