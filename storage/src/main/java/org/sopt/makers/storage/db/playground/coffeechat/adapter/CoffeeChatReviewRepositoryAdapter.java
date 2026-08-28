package org.sopt.makers.storage.db.playground.coffeechat.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.coffeechat.CoffeeChatReview;
import org.sopt.makers.domain.playground.coffeechat.port.CoffeeChatReviewRepositoryPort;
import org.sopt.makers.storage.db.playground.coffeechat.entity.AnonymousProfileImageEntity;
import org.sopt.makers.storage.db.playground.coffeechat.entity.CoffeeChatReviewEntity;
import org.sopt.makers.storage.db.playground.coffeechat.repository.AnonymousProfileImageJpaRepository;
import org.sopt.makers.storage.db.playground.coffeechat.repository.CoffeeChatReviewJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoffeeChatReviewRepositoryAdapter implements CoffeeChatReviewRepositoryPort {

  private final CoffeeChatReviewJpaRepository coffeeChatReviewJpaRepository;
  private final AnonymousProfileImageJpaRepository anonymousProfileImageJpaRepository;

  @Transactional
  @Override
  public void save(
      Long reviewerId, Long coffeeChatId, Long anonymousProfileImageId, String nickname, String content) {
    AnonymousProfileImageEntity imageEntity =
        anonymousProfileImageJpaRepository.findById(anonymousProfileImageId).orElseThrow();
    coffeeChatReviewJpaRepository.save(
        CoffeeChatReviewEntity.of(reviewerId, coffeeChatId, imageEntity, nickname, content));
  }

  @Override
  public boolean existsByReviewerIdAndCoffeeChatId(Long reviewerId, Long coffeeChatId) {
    return coffeeChatReviewJpaRepository.existsByReviewerIdAndCoffeeChatId(reviewerId, coffeeChatId);
  }

  @Override
  public List<CoffeeChatReview> findTop6ByOrderByIdDesc() {
    return coffeeChatReviewJpaRepository.findTop6ByOrderByIdDesc().stream()
        .map(CoffeeChatReviewEntity::toDomain)
        .toList();
  }
}
