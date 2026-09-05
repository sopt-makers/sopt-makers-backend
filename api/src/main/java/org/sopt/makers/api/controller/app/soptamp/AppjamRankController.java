package org.sopt.makers.api.controller.app.soptamp;

import static org.sopt.makers.api.controller.app.soptamp.AppjamRankSuccessCode.GET_RECENT_TEAM_RANKS;
import static org.sopt.makers.api.controller.app.soptamp.AppjamRankSuccessCode.GET_TODAY_TEAM_RANKS;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.app.soptamp.dto.AppjamRankResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamTeamSortType;
import org.sopt.makers.domain.app.soptamp.facade.AppjamRankFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/appjamrank")
@RequiredArgsConstructor
@Validated
public class AppjamRankController implements AppjamRankApi {

  private final AppjamRankFacade appjamRankFacade;

  @Override
  @GetMapping("/recent")
  public ResponseEntity<BaseResponse<?>> getRecentTeamRanks(
      @Min(1) @RequestParam(name = "size", defaultValue = "3") int size) {
    return ResponseFactory.success(
        GET_RECENT_TEAM_RANKS,
        AppjamRankResponse.AppjamtampRankListResponse.of(
            appjamRankFacade.findRecentTeamRanks(size)));
  }

  @Override
  @GetMapping("/today")
  public ResponseEntity<BaseResponse<?>> getTodayTeamRanks(
      @Min(1) @RequestParam(name = "size", defaultValue = "11") int size,
      @RequestParam(name = "sort", defaultValue = "NAME") AppjamTeamSortType sort) {
    return ResponseFactory.success(
        GET_TODAY_TEAM_RANKS,
        AppjamRankResponse.AppjamTodayRankListResponse.of(
            appjamRankFacade.findTodayTeamRanks(size, sort)));
  }
}
