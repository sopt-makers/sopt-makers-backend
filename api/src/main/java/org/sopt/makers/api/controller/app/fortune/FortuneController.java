package org.sopt.makers.api.controller.app.fortune;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.resolver.CurrentUserId;
import org.sopt.makers.api.controller.app.fortune.dto.FortuneCardResponse;
import org.sopt.makers.api.controller.app.fortune.dto.FortuneResponse;
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
  public ResponseEntity<FortuneResponse> getTodayFortuneWord(
      @CurrentUserId Long userId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate todayDate) {
    return ResponseEntity.ok(
        FortuneResponse.of(fortuneFacade.getTodayFortuneWord(userId, todayDate)));
  }

  @Override
  @GetMapping("/card/today")
  public ResponseEntity<FortuneCardResponse> getTodayFortuneCard(@CurrentUserId Long userId) {
    return ResponseEntity.ok(FortuneCardResponse.of(fortuneService.getTodayFortuneCard(userId)));
  }
}
