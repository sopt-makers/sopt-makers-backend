package org.sopt.makers.storage.db.app.fortune.querydsl;

import java.time.LocalDate;
import java.util.Optional;
import org.sopt.makers.storage.db.app.fortune.entity.FortuneCardEntity;

public interface FortuneCardQuerydslRepository {

  Optional<FortuneCardEntity> findTodayCardByUserId(Long userId, LocalDate todayDate);
}
