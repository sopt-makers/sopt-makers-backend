package org.sopt.makers.domain.app.fortune.port;

import java.time.LocalDate;
import java.util.Optional;
import org.sopt.makers.domain.app.fortune.FortuneCard;

public interface FortuneCardRepositoryPort {

  Optional<FortuneCard> findTodayCardByUserId(Long userId, LocalDate todayDate);
}
