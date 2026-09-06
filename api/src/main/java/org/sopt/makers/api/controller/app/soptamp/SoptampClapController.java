package org.sopt.makers.api.controller.app.soptamp;

import static org.sopt.makers.api.controller.app.soptamp.SoptampSuccessCode.ADD_CLAP;
import static org.sopt.makers.api.controller.app.soptamp.SoptampSuccessCode.GET_CLAPPERS;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.app.soptamp.dto.AddClapRequest;
import org.sopt.makers.api.controller.app.soptamp.dto.AddClapResponse;
import org.sopt.makers.api.controller.app.soptamp.dto.ClapUserListResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.soptamp.clap.service.ClapService;
import org.sopt.makers.domain.app.soptamp.facade.SoptampFacade;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/stamp")
@RequiredArgsConstructor
public class SoptampClapController implements SoptampClapApi {

  private static final int DEFAULT_PAGE_SIZE = 25;

  private final SoptampFacade soptampFacade;
  private final ClapService clapService;

  @Override
  @PostMapping("/{stampId}/clap")
  public ResponseEntity<BaseResponse<?>> addClap(
      @CurrentUserId Long userId,
      @PathVariable Long stampId,
      @Valid @RequestBody AddClapRequest request) {
    return ResponseFactory.success(
        ADD_CLAP,
        AddClapResponse.of(stampId, soptampFacade.addClap(userId, stampId, request.clapCount())));
  }

  @Override
  @GetMapping("/{stampId}/clappers")
  public ResponseEntity<BaseResponse<?>> getClappers(
      @CurrentUserId Long userId,
      @PathVariable Long stampId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "25") int size) {
    PageRequest pageable = PageRequest.of(Math.max(page, 0), size < 1 ? DEFAULT_PAGE_SIZE : size);
    return ResponseFactory.success(
        GET_CLAPPERS,
        ClapUserListResponse.of(clapService.getClapsOfMyStamp(userId, stampId, pageable)));
  }
}
