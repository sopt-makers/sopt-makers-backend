package org.sopt.makers.api.controller.app.soptamp;

import static org.sopt.makers.api.controller.app.soptamp.SoptampSuccessCode.GET_CURRENT_RANKS;
import static org.sopt.makers.api.controller.app.soptamp.SoptampSuccessCode.GET_CURRENT_RANKS_BY_PART;
import static org.sopt.makers.api.controller.app.soptamp.SoptampSuccessCode.GET_PART_RANKS;
import static org.sopt.makers.api.controller.app.soptamp.SoptampSuccessCode.GET_RANK_DETAIL;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.controller.app.soptamp.dto.PartRankResponse;
import org.sopt.makers.api.controller.app.soptamp.dto.RankDetailResponse;
import org.sopt.makers.api.controller.app.soptamp.dto.UserRankResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.app.soptamp.facade.SoptampFacade;
import org.sopt.makers.domain.app.soptamp.rank.service.RankService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/rank")
@RequiredArgsConstructor
public class SoptampRankController implements SoptampRankApi {

  private final RankService rankService;
  private final SoptampFacade soptampFacade;

  @Override
  @GetMapping("/current")
  public ResponseEntity<BaseResponse<List<UserRankResponse>>> findCurrentRanks() {
    return ResponseFactory.typedSuccess(
        GET_CURRENT_RANKS,
        rankService.findCurrentRanks().stream().map(UserRankResponse::of).toList());
  }

  @Override
  @GetMapping("/current/part/{part}")
  public ResponseEntity<BaseResponse<List<UserRankResponse>>> findCurrentRanksByPart(
      @PathVariable Part part) {
    return ResponseFactory.typedSuccess(
        GET_CURRENT_RANKS_BY_PART,
        rankService.findCurrentRanksByPart(part).stream().map(UserRankResponse::of).toList());
  }

  @Override
  @GetMapping("/part")
  public ResponseEntity<BaseResponse<List<PartRankResponse>>> findPartRanks() {
    return ResponseFactory.typedSuccess(
        GET_PART_RANKS, rankService.findAllPartRanks().stream().map(PartRankResponse::of).toList());
  }

  @Override
  @GetMapping("/detail")
  public ResponseEntity<BaseResponse<RankDetailResponse>> findUserMissionsByNickname(
      @RequestParam String nickname) {
    return ResponseFactory.typedSuccess(
        GET_RANK_DETAIL, RankDetailResponse.of(soptampFacade.findUserMissionsByNickname(nickname)));
  }
}
