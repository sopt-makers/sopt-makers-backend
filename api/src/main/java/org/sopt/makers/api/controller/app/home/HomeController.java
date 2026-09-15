package org.sopt.makers.api.controller.app.home;

import static org.sopt.makers.api.controller.app.home.HomeSuccessCode.GET_FLOATING_BUTTON;
import static org.sopt.makers.api.controller.app.home.HomeSuccessCode.GET_HOME_APP_SERVICES;
import static org.sopt.makers.api.controller.app.home.HomeSuccessCode.GET_HOME_DESCRIPTION;
import static org.sopt.makers.api.controller.app.home.HomeSuccessCode.GET_POPULAR_POSTS;
import static org.sopt.makers.api.controller.app.home.HomeSuccessCode.GET_RECENT_POSTS;
import static org.sopt.makers.api.controller.app.home.HomeSuccessCode.GET_REVIEW_FORM;
import static org.sopt.makers.api.controller.app.home.HomeSuccessCode.GET_TAB_APP_SERVICES;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.app.home.dto.AppServiceEntryStatusResponse;
import org.sopt.makers.api.controller.app.home.dto.FloatingButtonResponse;
import org.sopt.makers.api.controller.app.home.dto.HomeAppServiceResponse;
import org.sopt.makers.api.controller.app.home.dto.HomeDescriptionResponse;
import org.sopt.makers.api.controller.app.home.dto.PlaygroundPopularPostsResponse;
import org.sopt.makers.api.controller.app.home.dto.PlaygroundRecentPostsResponse;
import org.sopt.makers.api.controller.app.home.dto.ReviewFormResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.home.facade.HomeFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/home")
@RequiredArgsConstructor
public class HomeController implements HomeApi {

  private final HomeFacade homeFacade;

  @Override
  @GetMapping("/description")
  public ResponseEntity<BaseResponse<HomeDescriptionResponse>> getHomeMainDescription(
      @CurrentUserId Long userId) {
    return ResponseFactory.typedSuccess(
        GET_HOME_DESCRIPTION,
        HomeDescriptionResponse.of(homeFacade.getHomeMainDescription(userId)));
  }

  @Override
  @GetMapping("/app-service")
  public ResponseEntity<BaseResponse<HomeAppServiceResponse>> getHomeAppService(
      @CurrentUserId(required = false) Long userId) {
    return ResponseFactory.typedSuccess(
        GET_HOME_APP_SERVICES, HomeAppServiceResponse.of(homeFacade.getHomeAppServices(userId)));
  }

  @Override
  @GetMapping("/tab-app-service")
  public ResponseEntity<BaseResponse<List<AppServiceEntryStatusResponse>>> getTabAppService(
      @CurrentUserId(required = false) Long userId) {
    return ResponseFactory.typedSuccess(
        GET_TAB_APP_SERVICES,
        homeFacade.checkTabAppServiceEntryStatus(userId).stream()
            .map(AppServiceEntryStatusResponse::of)
            .toList());
  }

  @Override
  @GetMapping("/posts/latest")
  public ResponseEntity<BaseResponse<PlaygroundRecentPostsResponse>> getRecentPosts() {
    return ResponseFactory.typedSuccess(
        GET_RECENT_POSTS, PlaygroundRecentPostsResponse.of(homeFacade.getPlaygroundRecentPosts()));
  }

  @Override
  @GetMapping("/posts/popular")
  public ResponseEntity<BaseResponse<PlaygroundPopularPostsResponse>> getPopularPosts() {
    return ResponseFactory.typedSuccess(
        GET_POPULAR_POSTS,
        PlaygroundPopularPostsResponse.of(homeFacade.getPlaygroundPopularPosts()));
  }

  @Override
  @GetMapping("/floating-button")
  public ResponseEntity<BaseResponse<FloatingButtonResponse>> getFloatingButtonInfo(
      @CurrentUserId(required = false) Long userId) {
    return ResponseFactory.typedSuccess(
        GET_FLOATING_BUTTON, FloatingButtonResponse.of(homeFacade.getFloatingButtonInfo(userId)));
  }

  @Override
  @GetMapping("/review-form")
  public ResponseEntity<BaseResponse<ReviewFormResponse>> getReviewForm(
      @CurrentUserId(required = false) Long userId) {
    return ResponseFactory.typedSuccess(
        GET_REVIEW_FORM, ReviewFormResponse.of(homeFacade.getReviewFormInfo(userId)));
  }
}
