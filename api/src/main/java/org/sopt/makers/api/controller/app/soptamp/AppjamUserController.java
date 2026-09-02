package org.sopt.makers.api.controller.app.soptamp;

import static org.sopt.makers.api.controller.app.soptamp.AppjamUserSuccessCode.GET_APPJAM_INFO;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.app.soptamp.dto.AppjamUserResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.soptamp.facade.AppjamtampFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/user")
@RequiredArgsConstructor
public class AppjamUserController implements AppjamUserApi {

  private final AppjamtampFacade appjamtampFacade;

  @Deprecated
  @Override
  @GetMapping("/appjam-info")
  public ResponseEntity<BaseResponse<?>> getTeamInfo(@CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_APPJAM_INFO,
        AppjamUserResponse.AppjamStatusResponse.of(appjamtampFacade.getAppjamStatus(userId)));
  }
}
