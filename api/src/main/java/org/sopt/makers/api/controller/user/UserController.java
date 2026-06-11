package org.sopt.makers.api.controller.user;

import static org.sopt.makers.api.controller.user.UserSuccessCode.UPDATE_USER_PROFILE;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.user.dto.UserRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.auth.facade.AuthFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {
  private final AuthFacade authFacade;

  @PutMapping("")
  public ResponseEntity<BaseResponse<?>> updateUserProfile(
      @CurrentUserId Long userId, @Valid @RequestBody UserRequest.UserProfileInfo request) {
    authFacade.updateProfile(userId, request.toCommand());
    return ResponseFactory.success(UPDATE_USER_PROFILE);
  }
}
