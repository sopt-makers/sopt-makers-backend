package org.sopt.makers.api.controller.app.soptamp;

import static org.sopt.makers.api.controller.app.soptamp.SoptampSuccessCode.EDIT_PROFILE_MESSAGE;
import static org.sopt.makers.api.controller.app.soptamp.SoptampSuccessCode.GET_SOPTAMP_USER;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.app.soptamp.dto.EditProfileMessageRequest;
import org.sopt.makers.api.controller.app.soptamp.dto.ProfileMessageResponse;
import org.sopt.makers.api.controller.app.soptamp.dto.SoptampUserResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.soptamp.service.SoptampUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/user")
@RequiredArgsConstructor
public class SoptampUserController implements SoptampUserApi {

  private final SoptampUserService soptampUserService;

  @Override
  @GetMapping("/soptamp")
  public ResponseEntity<BaseResponse<?>> getSoptampUser(@CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_SOPTAMP_USER, SoptampUserResponse.of(soptampUserService.getSoptampUser(userId)));
  }

  @Override
  @PatchMapping("/profile-message")
  public ResponseEntity<BaseResponse<?>> editProfileMessage(
      @CurrentUserId Long userId, @Valid @RequestBody EditProfileMessageRequest request) {
    return ResponseFactory.success(
        EDIT_PROFILE_MESSAGE,
        ProfileMessageResponse.of(
            soptampUserService.editProfileMessage(userId, request.profileMessage())));
  }
}
