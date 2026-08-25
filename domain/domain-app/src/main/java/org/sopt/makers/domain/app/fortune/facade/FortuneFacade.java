package org.sopt.makers.domain.app.fortune.facade;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.fortune.FortuneWord;
import org.sopt.makers.domain.app.fortune.service.FortuneService;
import org.sopt.makers.domain.user.port.FortuneUserPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FortuneFacade {

  private final FortuneService fortuneService;
  private final FortuneUserPort fortuneUserPort;

  public TodayFortuneWord getTodayFortuneWord(Long userId, LocalDate todayDate) {
    FortuneWord fortuneWord = fortuneService.getTodayFortuneWord(userId, todayDate);
    String userName = fortuneUserPort.getName(userId);
    return new TodayFortuneWord(userName, fortuneWord.title());
  }

  public record TodayFortuneWord(String userName, String title) {}
}
