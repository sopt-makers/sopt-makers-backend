package org.sopt.makers.api.controller.playground.review;

import static org.sopt.makers.api.controller.playground.review.ActivityReviewSuccessCode.SUCCESS_CREATE_ACTIVITY_REVIEW;
import static org.sopt.makers.api.controller.playground.review.ActivityReviewSuccessCode.SUCCESS_GET_ACTIVITY_REVIEWS;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.playground.review.dto.CreateActivityReviewRequest;
import org.sopt.makers.api.controller.playground.review.dto.PagedActivityReviewResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.playground.review.service.ActivityReviewCommandService;
import org.sopt.makers.domain.playground.review.service.ActivityReviewQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/review")
public class ActivityReviewController implements ActivityReviewApi {

  private final ActivityReviewCommandService activityReviewCommandService;
  private final ActivityReviewQueryService activityReviewQueryService;

  @Override
  @PostMapping
  public ResponseEntity<BaseResponse<?>> createActivityReview(
      @CurrentUserId Long userId, @Valid @RequestBody CreateActivityReviewRequest request) {
    activityReviewCommandService.createActivityReview(userId, request.content());
    return ResponseFactory.success(SUCCESS_CREATE_ACTIVITY_REVIEW);
  }

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getActivityReviews(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    return ResponseFactory.success(
        SUCCESS_GET_ACTIVITY_REVIEWS,
        PagedActivityReviewResponse.from(
            activityReviewQueryService.getActivityReviews(page, size)));
  }
}
