package org.sopt.makers.storage.db.playground.coffeechat.repository;

import java.util.List;
import org.sopt.makers.storage.db.playground.coffeechat.entity.CoffeeChatReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoffeeChatReviewJpaRepository
    extends JpaRepository<CoffeeChatReviewEntity, Long> {

  boolean existsByReviewerIdAndCoffeeChatId(Long reviewerId, Long coffeeChatId);

  List<CoffeeChatReviewEntity> findTop6ByOrderByIdDesc();
}
