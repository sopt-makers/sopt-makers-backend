package org.sopt.makers.domain.app.fortune.port;

import java.util.Optional;
import org.sopt.makers.domain.app.fortune.FortuneCard;

public interface FortuneCardRepositoryPort {

  Optional<FortuneCard> findTodayCardByUserId(Long userId);
}
