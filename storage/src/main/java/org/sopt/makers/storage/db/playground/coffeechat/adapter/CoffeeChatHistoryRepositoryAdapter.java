package org.sopt.makers.storage.db.playground.coffeechat.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.coffeechat.port.CoffeeChatHistoryRepositoryPort;
import org.sopt.makers.storage.db.playground.coffeechat.entity.CoffeeChatHistoryEntity;
import org.sopt.makers.storage.db.playground.coffeechat.repository.CoffeeChatHistoryJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoffeeChatHistoryRepositoryAdapter implements CoffeeChatHistoryRepositoryPort {

  private final CoffeeChatHistoryJpaRepository coffeeChatHistoryJpaRepository;

  @Transactional
  @Override
  public void save(Long receiverId, Long senderId, String requestContent) {
    coffeeChatHistoryJpaRepository.save(
        CoffeeChatHistoryEntity.of(receiverId, senderId, requestContent));
  }

  @Override
  public long countBySenderId(Long senderId) {
    return coffeeChatHistoryJpaRepository.countBySenderId(senderId);
  }

  @Override
  public long countByReceiverId(Long receiverId) {
    return coffeeChatHistoryJpaRepository.countByReceiverId(receiverId);
  }

  @Override
  public boolean existsByReceiverIdAndSenderId(Long receiverId, Long senderId) {
    return coffeeChatHistoryJpaRepository.existsByReceiverIdAndSenderId(receiverId, senderId);
  }
}
