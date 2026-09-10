package org.sopt.makers.storage.db.playground.coffeechat.repository;

import org.sopt.makers.storage.db.playground.coffeechat.entity.CoffeeChatHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoffeeChatHistoryJpaRepository
    extends JpaRepository<CoffeeChatHistoryEntity, Long> {

  long countByReceiverId(Long receiverId);

  long countBySenderId(Long senderId);
}
