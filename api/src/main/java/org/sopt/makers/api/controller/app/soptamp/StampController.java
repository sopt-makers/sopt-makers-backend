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
  public ResponseEntity<BaseResponse<?>> getStamp(
      @CurrentUserId Long userId, @Valid @ModelAttribute StampRequest.FindStampRequest request) {
    return ResponseFactory.success(
        GET_STAMP,
        StampResponse.StampView.of(
            soptampFacade.getStamp(userId, request.missionId(), request.nickname())));
  }

  @Override
  @PostMapping
  public ResponseEntity<BaseResponse<?>> registerStamp(
      @CurrentUserId Long userId, @Valid @RequestBody StampRequest.RegisterStampRequest request) {
    return ResponseFactory.success(
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
  public ResponseEntity<BaseResponse<?>> editStamp(
      @CurrentUserId Long userId, @Valid @RequestBody StampRequest.EditStampRequest request) {
    return ResponseFactory.success(
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
  public ResponseEntity<BaseResponse<?>> deleteStamp(
      @CurrentUserId Long userId, @PathVariable Long stampId) {
    soptampFacade.deleteStamp(userId, stampId);
    return ResponseFactory.success(DELETE_STAMP);
  }

  @Override
  @DeleteMapping("/all")
  public ResponseEntity<BaseResponse<?>> deleteAllStamps(@CurrentUserId Long userId) {
    soptampFacade.deleteAllStamps(userId);
    return ResponseFactory.success(DELETE_ALL_STAMPS);
  }

  @Override
  @GetMapping("/report")
  public ResponseEntity<BaseResponse<?>> getReportUrl() {
    return ResponseFactory.success(
        GET_REPORT_URL, StampResponse.SoptampReportResponse.of(soptampFacade.getReportUrl()));
  }
}
