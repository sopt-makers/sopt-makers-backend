package org.sopt.makers.api.controller.app.soptamp;

import static org.sopt.makers.api.controller.app.soptamp.AppjamtampSuccessCode.GET_APPJAM_MISSIONS;
import static org.sopt.makers.api.controller.app.soptamp.AppjamtampSuccessCode.GET_APPJAM_STAMP;
import static org.sopt.makers.api.controller.app.soptamp.AppjamtampSuccessCode.REGISTER_APPJAM_STAMP;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.app.soptamp.dto.AppjamtampRequest;
import org.sopt.makers.api.controller.app.soptamp.dto.AppjamtampResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.soptamp.appjam.TeamNumber;
import org.sopt.makers.domain.app.soptamp.facade.AppjamtampFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/appjamtamp")
@RequiredArgsConstructor
@Validated
public class AppjamtampController implements AppjamtampApi {

  private final AppjamtampFacade appjamtampFacade;

  @Override
  @GetMapping("/mission")
  public ResponseEntity<BaseResponse<?>> getMissions(
      @CurrentUserId Long userId,
      @RequestParam(name = "teamNumber", required = false) TeamNumber teamNumber,
      @RequestParam(name = "isCompleted", required = false) Boolean isCompleted) {
    return ResponseFactory.success(
        GET_APPJAM_MISSIONS,
        AppjamtampResponse.AppjamMissionResponses.of(
            appjamtampFacade.getTeamMissions(userId, teamNumber, isCompleted)));
  }

  @Override
  @GetMapping("/stamp")
  public ResponseEntity<BaseResponse<?>> getStamp(
      @CurrentUserId Long userId,
      @Valid @ModelAttribute AppjamtampRequest.FindStampRequest request) {
    return ResponseFactory.success(
        GET_APPJAM_STAMP,
        AppjamtampResponse.AppjamtampView.of(
            appjamtampFacade.getAppjamtamps(userId, request.missionId(), request.nickname())));
  }

  @Override
  @PostMapping("/stamp")
  public ResponseEntity<BaseResponse<?>> registerStamp(
      @CurrentUserId Long userId,
      @Valid @RequestBody AppjamtampRequest.RegisterStampRequest request) {
    return ResponseFactory.success(
        REGISTER_APPJAM_STAMP,
        AppjamtampResponse.StampMain.of(
            appjamtampFacade.uploadStamp(
                userId,
                request.missionId(),
                request.contents(),
                request.image(),
                request.activityDate())));
  }
}
