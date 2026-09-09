package org.sopt.makers.storage.db.playground.coffeechat.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.member.profile.port.CoffeeChatHistoryPort;
import org.sopt.makers.storage.db.playground.coffeechat.repository.CoffeeChatHistoryJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoffeeChatHistoryAdapter implements CoffeeChatHistoryPort {

  private final CoffeeChatHistoryJpaRepository coffeeChatHistoryJpaRepository;

  @Override
  public long countReceivedBy(Long userId) {
    return coffeeChatHistoryJpaRepository.countByReceiverId(userId);
  }

  @Override
  public long countSentBy(Long userId) {
    return coffeeChatHistoryJpaRepository.countBySenderId(userId);
  }
}
