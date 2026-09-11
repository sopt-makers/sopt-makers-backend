package org.sopt.makers.api.controller.app.soptamp;

import static org.sopt.makers.api.controller.app.soptamp.StampSuccessCode.DELETE_ALL_STAMPS;
import static org.sopt.makers.api.controller.app.soptamp.StampSuccessCode.DELETE_STAMP;
import static org.sopt.makers.api.controller.app.soptamp.StampSuccessCode.EDIT_STAMP;
import static org.sopt.makers.api.controller.app.soptamp.StampSuccessCode.GET_REPORT_URL;
import static org.sopt.makers.api.controller.app.soptamp.StampSuccessCode.GET_STAMP;
import static org.sopt.makers.api.controller.app.soptamp.StampSuccessCode.REGISTER_STAMP;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.app.soptamp.dto.StampRequest;
import org.sopt.makers.api.controller.app.soptamp.dto.StampResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.soptamp.facade.SoptampFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/stamp")
@RequiredArgsConstructor
@Validated
public class StampController implements StampApi {

  private final SoptampFacade soptampFacade;

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<StampResponse.StampView>> getStamp(
      @CurrentUserId Long userId, @Valid @ModelAttribute StampRequest.FindStampRequest request) {
    return ResponseFactory.typedSuccess(
        GET_STAMP,
        StampResponse.StampView.of(
            soptampFacade.getStamp(userId, request.missionId(), request.nickname())));
  }

  @Override
  @PostMapping
  public ResponseEntity<BaseResponse<StampResponse.StampMain>> registerStamp(
      @CurrentUserId Long userId, @Valid @RequestBody StampRequest.RegisterStampRequest request) {
    return ResponseFactory.typedSuccess(
        REGISTER_STAMP,
        StampResponse.StampMain.of(
            soptampFacade.registerStamp(
                userId,
                request.missionId(),
                request.contents(),
                request.image(),
                request.activityDate())));
  }

  @Override
  @PutMapping
  public ResponseEntity<BaseResponse<StampResponse.StampId>> editStamp(
      @CurrentUserId Long userId, @Valid @RequestBody StampRequest.EditStampRequest request) {
    return ResponseFactory.typedSuccess(
        EDIT_STAMP,
        StampResponse.StampId.of(
            soptampFacade.editStamp(
                userId,
                request.missionId(),
                request.contents(),
                request.image(),
                request.activityDate())));
  }

  @Override
  @DeleteMapping("/{stampId}")
  public ResponseEntity<BaseResponse<Void>> deleteStamp(
      @CurrentUserId Long userId, @PathVariable Long stampId) {
    soptampFacade.deleteStamp(userId, stampId);
    return ResponseFactory.typedSuccess(DELETE_STAMP);
  }

  @Override
  @DeleteMapping("/all")
  public ResponseEntity<BaseResponse<Void>> deleteAllStamps(@CurrentUserId Long userId) {
    soptampFacade.deleteAllStamps(userId);
    return ResponseFactory.typedSuccess(DELETE_ALL_STAMPS);
  }

  @Override
  @GetMapping("/report")
  public ResponseEntity<BaseResponse<StampResponse.SoptampReportResponse>> getReportUrl() {
    return ResponseFactory.typedSuccess(
        GET_REPORT_URL, StampResponse.SoptampReportResponse.of(soptampFacade.getReportUrl()));
  }
}
