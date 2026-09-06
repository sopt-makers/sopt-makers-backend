package org.sopt.makers.api.controller.app.user;

import static org.sopt.makers.api.controller.app.user.AppUserSuccessCode.GET_GENERATION;
import static org.sopt.makers.api.controller.app.user.AppUserSuccessCode.GET_MAIN_VIEW;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.app.user.dto.GenerationResponse;
import org.sopt.makers.api.controller.app.user.dto.MainViewResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.home.facade.HomeFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/user")
@RequiredArgsConstructor
public class AppUserController implements AppUserApi {

  private final HomeFacade homeFacade;

  @Override
  @GetMapping("/main")
  public ResponseEntity<BaseResponse<?>> getMainViewInfo(
      @CurrentUserId(required = false) Long userId) {
    return ResponseFactory.success(
        GET_MAIN_VIEW, MainViewResponse.of(homeFacade.getMainViewInfo(userId)));
  }

  @Override
  @GetMapping("/generation")
  public ResponseEntity<BaseResponse<?>> getGenerationInfo(@CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_GENERATION, GenerationResponse.of(homeFacade.getUserActiveInfo(userId)));
  }
}
