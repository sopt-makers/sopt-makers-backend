package org.sopt.makers.api.controller.app.soptamp;

import static org.sopt.makers.api.controller.app.soptamp.MissionSuccessCode.GET_ALL_MISSIONS;
import static org.sopt.makers.api.controller.app.soptamp.MissionSuccessCode.GET_COMPLETED_MISSIONS;
import static org.sopt.makers.api.controller.app.soptamp.MissionSuccessCode.GET_INCOMPLETE_MISSIONS;
import static org.sopt.makers.api.controller.app.soptamp.MissionSuccessCode.REGISTER_MISSION;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.app.soptamp.dto.MissionRequest;
import org.sopt.makers.api.controller.app.soptamp.dto.MissionResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.soptamp.facade.SoptampFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/mission")
@RequiredArgsConstructor
@Validated
public class MissionController implements MissionApi {

  private final SoptampFacade soptampFacade;

  @Override
  @GetMapping("/all")
  public ResponseEntity<BaseResponse<?>> getAllMissions(@CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_ALL_MISSIONS,
        soptampFacade.getAllMissionsWithCompleteness(userId).stream()
            .map(MissionResponse.Completeness::of)
            .toList());
  }

  @Override
  @PostMapping("")
  public ResponseEntity<BaseResponse<?>> registerMission(
      @Valid @RequestBody MissionRequest.RegisterMissionRequest request) {
    return ResponseFactory.success(
        REGISTER_MISSION,
        MissionResponse.MissionId.of(
            soptampFacade.registerMission(request.title(), request.level(), request.image())));
  }

  @Override
  @GetMapping("/complete")
  public ResponseEntity<BaseResponse<?>> getCompletedMissions(@CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_COMPLETED_MISSIONS,
        soptampFacade.getCompletedMissions(userId).stream()
            .map(MissionResponse.MissionMain::of)
            .toList());
  }

  @Override
  @GetMapping("/incomplete")
  public ResponseEntity<BaseResponse<?>> getIncompleteMissions(@CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_INCOMPLETE_MISSIONS,
        soptampFacade.getIncompleteMissions(userId).stream()
            .map(MissionResponse.MissionMain::of)
            .toList());
  }
}
