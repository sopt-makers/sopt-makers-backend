package org.sopt.makers.api.controller.crew.flash;

import static org.sopt.makers.api.controller.crew.flash.FlashSuccessCode.CREATE_FLASH;
import static org.sopt.makers.api.controller.crew.flash.FlashSuccessCode.GET_FLASH;
import static org.sopt.makers.api.controller.crew.flash.FlashSuccessCode.UPDATE_FLASH;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.crew.flash.dto.CreateFlashRequest;
import org.sopt.makers.api.controller.crew.flash.dto.CreateFlashResponse;
import org.sopt.makers.api.controller.crew.flash.dto.FlashDetailResponse;
import org.sopt.makers.api.controller.crew.flash.dto.UpdateFlashRequest;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.crew.flash.facade.FlashFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/flash/v2")
@RequiredArgsConstructor
public class FlashController implements FlashApi {

  private final FlashFacade flashFacade;

  @Override
  @PostMapping
  public ResponseEntity<BaseResponse<?>> createFlash(
      @Valid @RequestBody CreateFlashRequest request, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        CREATE_FLASH,
        CreateFlashResponse.from(flashFacade.createFlash(request.toCommand(), userId)));
  }

  @Override
  @GetMapping("/{meetingId}")
  public ResponseEntity<BaseResponse<?>> getFlash(
      @PathVariable Long meetingId, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_FLASH, FlashDetailResponse.from(flashFacade.getFlashDetail(meetingId, userId)));
  }

  @Override
  @PutMapping("/{meetingId}")
  public ResponseEntity<BaseResponse<?>> updateFlash(
      @PathVariable Long meetingId,
      @Valid @RequestBody UpdateFlashRequest request,
      @CurrentUserId Long userId) {
    flashFacade.updateFlash(meetingId, request.toCommand(), userId);
    return ResponseFactory.success(UPDATE_FLASH);
  }
}
