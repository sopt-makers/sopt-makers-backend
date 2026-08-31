package org.sopt.makers.api.controller.crew.soptmap;

import static org.sopt.makers.api.controller.crew.soptmap.SoptMapSuccessCode.CHECK_SOPT_MAP_EVENT;
import static org.sopt.makers.api.controller.crew.soptmap.SoptMapSuccessCode.CREATE_SOPT_MAP;
import static org.sopt.makers.api.controller.crew.soptmap.SoptMapSuccessCode.DELETE_SOPT_MAP;
import static org.sopt.makers.api.controller.crew.soptmap.SoptMapSuccessCode.GET_SOPT_MAP;
import static org.sopt.makers.api.controller.crew.soptmap.SoptMapSuccessCode.GET_SOPT_MAPS;
import static org.sopt.makers.api.controller.crew.soptmap.SoptMapSuccessCode.GET_SOPT_MAP_GIFT;
import static org.sopt.makers.api.controller.crew.soptmap.SoptMapSuccessCode.SEARCH_SUBWAY_STATION;
import static org.sopt.makers.api.controller.crew.soptmap.SoptMapSuccessCode.TOGGLE_SOPT_MAP_RECOMMEND;
import static org.sopt.makers.api.controller.crew.soptmap.SoptMapSuccessCode.UPDATE_SOPT_MAP;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.crew.soptmap.dto.CreateSoptMapResponse;
import org.sopt.makers.api.controller.crew.soptmap.dto.GetSoptMapsRequest;
import org.sopt.makers.api.controller.crew.soptmap.dto.SoptMapBodyRequest;
import org.sopt.makers.api.controller.crew.soptmap.dto.SoptMapDetailResponse;
import org.sopt.makers.api.controller.crew.soptmap.dto.SoptMapEventResponse;
import org.sopt.makers.api.controller.crew.soptmap.dto.SoptMapGiftResponse;
import org.sopt.makers.api.controller.crew.soptmap.dto.SoptMapPageResponse;
import org.sopt.makers.api.controller.crew.soptmap.dto.SubwayStationResponse;
import org.sopt.makers.api.controller.crew.soptmap.dto.ToggleSoptMapRecommendResponse;
import org.sopt.makers.core.pagination.PageQuery;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.crew.soptmap.service.SoptMapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/map")
@RequiredArgsConstructor
public class SoptMapController implements SoptMapApi {

  private final SoptMapService soptMapService;

  @Override
  @PostMapping
  public ResponseEntity<BaseResponse<?>> create(
      @Valid @RequestBody SoptMapBodyRequest request, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        CREATE_SOPT_MAP,
        CreateSoptMapResponse.from(soptMapService.create(request.toCreateCommand(), userId)));
  }

  @Override
  @PutMapping("/{soptMapId}")
  public ResponseEntity<BaseResponse<?>> update(
      @PathVariable Long soptMapId,
      @Valid @RequestBody SoptMapBodyRequest request,
      @CurrentUserId Long userId) {
    return ResponseFactory.success(
        UPDATE_SOPT_MAP,
        CreateSoptMapResponse.updated(
            soptMapService.update(soptMapId, request.toUpdateCommand(), userId)));
  }

  @Override
  @DeleteMapping("/{soptMapId}")
  public ResponseEntity<BaseResponse<?>> delete(
      @PathVariable Long soptMapId, @CurrentUserId Long userId) {
    soptMapService.delete(soptMapId, userId);
    return ResponseFactory.success(DELETE_SOPT_MAP);
  }

  @Override
  @GetMapping("/search/subway")
  public ResponseEntity<BaseResponse<?>> searchSubwayStations(
      @RequestParam(required = false) String keyword) {
    return ResponseFactory.success(
        SEARCH_SUBWAY_STATION,
        new SubwayStationResponse.ListResponse(
            soptMapService.searchStations(keyword).stream()
                .map(SubwayStationResponse::from)
                .toList()));
  }

  @Override
  @GetMapping
  public ResponseEntity<BaseResponse<?>> getSoptMaps(
      @Valid @ModelAttribute GetSoptMapsRequest request, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_SOPT_MAPS,
        SoptMapPageResponse.from(
            soptMapService.search(
                userId,
                request.categories(),
                request.sortTypeOrDefault(),
                request.stationKeyword(),
                new PageQuery(request.pageOrDefault(), request.takeOrDefault()))));
  }

  @Override
  @GetMapping("/{soptMapId}")
  public ResponseEntity<BaseResponse<?>> getSoptMap(
      @PathVariable Long soptMapId, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_SOPT_MAP, SoptMapDetailResponse.from(soptMapService.getDetail(soptMapId, userId)));
  }

  @Override
  @PutMapping("/toggle/recommend/{soptMapId}")
  public ResponseEntity<BaseResponse<?>> toggleRecommend(
      @PathVariable Long soptMapId, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        TOGGLE_SOPT_MAP_RECOMMEND,
        ToggleSoptMapRecommendResponse.from(soptMapService.toggleRecommend(soptMapId, userId)));
  }

  @Override
  @GetMapping("/event/{soptMapId}")
  public ResponseEntity<BaseResponse<?>> checkEvent(
      @PathVariable Long soptMapId, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        CHECK_SOPT_MAP_EVENT,
        new SoptMapEventResponse(soptMapService.checkEventWinning(soptMapId, userId)));
  }

  @Override
  @GetMapping("/gift/{soptMapId}")
  public ResponseEntity<BaseResponse<?>> getGift(
      @PathVariable Long soptMapId, @CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_SOPT_MAP_GIFT, SoptMapGiftResponse.from(soptMapService.getGift(soptMapId, userId)));
  }
}
