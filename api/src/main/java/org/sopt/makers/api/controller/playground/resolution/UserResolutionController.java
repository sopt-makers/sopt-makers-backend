package org.sopt.makers.api.controller.playground.resolution;

import static org.sopt.makers.api.controller.playground.resolution.UserResolutionSuccessCode.CREATE_RESOLUTION;
import static org.sopt.makers.api.controller.playground.resolution.UserResolutionSuccessCode.DELETE_RESOLUTION;
import static org.sopt.makers.api.controller.playground.resolution.UserResolutionSuccessCode.GET_LUCKY_PICK_RESULT;
import static org.sopt.makers.api.controller.playground.resolution.UserResolutionSuccessCode.GET_RESOLUTION;
import static org.sopt.makers.api.controller.playground.resolution.UserResolutionSuccessCode.VALIDATE_RESOLUTION;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.playground.resolution.dto.LuckyPickResponse;
import org.sopt.makers.api.controller.playground.resolution.dto.ResolutionResponse;
import org.sopt.makers.api.controller.playground.resolution.dto.ResolutionSaveRequest;
import org.sopt.makers.api.controller.playground.resolution.dto.ResolutionValidResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.playground.resolution.service.LuckyPickService;
import org.sopt.makers.domain.playground.resolution.service.UserResolutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/resolution")
public class UserResolutionController implements UserResolutionApi {

  private final UserResolutionService userResolutionService;
  private final LuckyPickService luckyPickService;

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getResolution(@CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_RESOLUTION, ResolutionResponse.from(userResolutionService.getResolution(userId)));
  }

  @Override
  @PostMapping
  public ResponseEntity<BaseResponse<?>> createResolution(
      @CurrentUserId Long userId, @Valid @RequestBody ResolutionSaveRequest request) {
    userResolutionService.createResolution(userId, request.content(), request.toTags());
    return ResponseFactory.success(CREATE_RESOLUTION);
  }

  @Override
  @GetMapping("/validation")
  public ResponseEntity<BaseResponse<?>> validation(@CurrentUserId Long userId) {
    return ResponseFactory.success(
        VALIDATE_RESOLUTION,
        new ResolutionValidResponse(userResolutionService.isRegistered(userId)));
  }

  @Override
  @DeleteMapping
  public ResponseEntity<BaseResponse<?>> deleteResolution(@CurrentUserId Long userId) {
    userResolutionService.deleteResolution(userId);
    return ResponseFactory.success(DELETE_RESOLUTION);
  }

  @Override
  @GetMapping("/lucky-pick")
  public ResponseEntity<BaseResponse<?>> getLuckyPickResult(@CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_LUCKY_PICK_RESULT,
        new LuckyPickResponse(luckyPickService.checkLuckyPickResult(userId)));
  }
}
