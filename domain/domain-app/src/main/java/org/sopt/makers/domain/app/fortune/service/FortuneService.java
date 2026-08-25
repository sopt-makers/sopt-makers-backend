package org.sopt.makers.domain.app.fortune.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.fortune.FortuneCard;
import org.sopt.makers.domain.app.fortune.FortuneWord;
import org.sopt.makers.domain.app.fortune.UserFortune;
import org.sopt.makers.domain.app.fortune.exception.FortuneException;
import org.sopt.makers.domain.app.fortune.exception.FortuneFailure;
import org.sopt.makers.domain.app.fortune.port.FortuneCardRepositoryPort;
import org.sopt.makers.domain.app.fortune.port.FortuneWordRepositoryPort;
import org.sopt.makers.domain.app.fortune.port.UserFortuneRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FortuneService {

  private static final ZoneId KST = ZoneId.of("Asia/Seoul");

  private final FortuneCardRepositoryPort fortuneCardRepositoryPort;
  private final FortuneWordRepositoryPort fortuneWordRepositoryPort;
  private final UserFortuneRepositoryPort userFortuneRepositoryPort;
  private final FortuneWordIdGenerator fortuneWordIdGenerator;
  private final Clock clock;

  @Transactional
  public FortuneWord getTodayFortuneWord(Long userId, LocalDate todayDate) {
    UserFortune userFortune = resolveTodayUserFortune(userId, todayDate);
    return fortuneWordRepositoryPort
        .findById(userFortune.fortuneWordId())
        .orElseThrow(() -> new FortuneException(FortuneFailure.NOT_FOUND_FORTUNE_WORD));
  }

  public FortuneCard getTodayFortuneCard(Long userId) {
    return fortuneCardRepositoryPort
        .findTodayCardByUserId(userId, today())
        .orElseThrow(() -> new FortuneException(FortuneFailure.NOT_FOUND_FORTUNE_FROM_USER));
  }

  public boolean isExistTodayFortune(Long userId) {
    return userFortuneRepositoryPort
        .findByUserId(userId)
        .map(userFortune -> userFortune.checkedAt().equals(today()))
        .orElse(false);
  }

  private LocalDate today() {
    return LocalDate.now(clock.withZone(KST));
  }

  private UserFortune resolveTodayUserFortune(Long userId, LocalDate todayDate) {
    return userFortuneRepositoryPort
        .findByUserId(userId)
        .map(userFortune -> refreshIfNeeded(userFortune, todayDate))
        .orElseGet(
            () ->
                userFortuneRepositoryPort.save(
                    UserFortune.create(userId, fortuneWordIdGenerator.generate(), todayDate)));
  }

  private UserFortune refreshIfNeeded(UserFortune userFortune, LocalDate todayDate) {
    if (userFortune.needsRefresh(todayDate)) {
      return userFortuneRepositoryPort.save(
          userFortune.refreshed(fortuneWordIdGenerator.generate(), todayDate));
    }
    return userFortune;
  }
}
