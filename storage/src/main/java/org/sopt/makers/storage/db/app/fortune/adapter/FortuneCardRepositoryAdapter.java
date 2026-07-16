package org.sopt.makers.storage.db.app.fortune.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.fortune.FortuneCard;
import org.sopt.makers.domain.app.fortune.port.FortuneCardRepositoryPort;
import org.sopt.makers.storage.db.app.fortune.entity.FortuneCardEntity;
import org.sopt.makers.storage.db.app.fortune.querydsl.FortuneCardQuerydslRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FortuneCardRepositoryAdapter implements FortuneCardRepositoryPort {

  private final FortuneCardQuerydslRepository fortuneCardQuerydslRepository;

  @Override
  public Optional<FortuneCard> findTodayCardByUserId(Long userId) {
    return fortuneCardQuerydslRepository
        .findTodayCardByUserId(userId)
        .map(FortuneCardEntity::toDomain);
  }
}
