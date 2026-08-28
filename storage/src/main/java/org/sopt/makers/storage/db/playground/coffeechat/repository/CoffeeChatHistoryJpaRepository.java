package org.sopt.makers.storage.db.playground.coffeechat.repository;

import org.sopt.makers.storage.db.playground.coffeechat.entity.CoffeeChatHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoffeeChatHistoryJpaRepository
    extends JpaRepository<CoffeeChatHistoryEntity, Long> {

  long countBySenderId(Long senderId);

  long countByReceiverId(Long receiverId);

  boolean existsByReceiverIdAndSenderId(Long receiverId, Long senderId);
}
