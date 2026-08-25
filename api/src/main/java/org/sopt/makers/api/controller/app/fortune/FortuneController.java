package org.sopt.makers.api.controller.app.fortune;

import static org.sopt.makers.api.controller.app.fortune.FortuneSuccessCode.GET_TODAY_FORTUNE_CARD;
import static org.sopt.makers.api.controller.app.fortune.FortuneSuccessCode.GET_TODAY_FORTUNE_WORD;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.factory.ResponseFactory;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.app.fortune.dto.FortuneCardResponse;
import org.sopt.makers.api.controller.app.fortune.dto.FortuneResponse;
import org.sopt.makers.core.response.BaseResponse;
import org.sopt.makers.domain.app.fortune.facade.FortuneFacade;
import org.sopt.makers.domain.app.fortune.service.FortuneService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/fortune")
@RequiredArgsConstructor
public class FortuneController implements FortuneApi {

  private final FortuneFacade fortuneFacade;
  private final FortuneService fortuneService;

  @Override
  @GetMapping("/word")
  public ResponseEntity<BaseResponse<?>> getTodayFortuneWord(
      @CurrentUserId Long userId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate todayDate) {
    return ResponseFactory.success(
        GET_TODAY_FORTUNE_WORD,
        FortuneResponse.of(fortuneFacade.getTodayFortuneWord(userId, todayDate)));
  }

  @Override
  @GetMapping("/card/today")
  public ResponseEntity<BaseResponse<?>> getTodayFortuneCard(@CurrentUserId Long userId) {
    return ResponseFactory.success(
        GET_TODAY_FORTUNE_CARD, FortuneCardResponse.of(fortuneService.getTodayFortuneCard(userId)));
  }
}
