package org.sopt.makers.storage.db.playground.coffeechat.adapter;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.member.profile.port.CoffeeChatActivationPort;
import org.sopt.makers.storage.db.playground.coffeechat.entity.CoffeeChatEntity;
import org.sopt.makers.storage.db.playground.coffeechat.repository.CoffeeChatJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoffeeChatActivationAdapter implements CoffeeChatActivationPort {

  private final CoffeeChatJpaRepository coffeeChatJpaRepository;

  @Override
  public boolean isCoffeeChatActive(Long userId) {
    return coffeeChatJpaRepository.existsByMemberIdAndIsCoffeeChatActivateTrue(userId);
  }

  @Override
  public Set<Long> findActiveUserIds(Collection<Long> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      return Set.of();
    }
    return coffeeChatJpaRepository.findAllByMemberIdInAndIsCoffeeChatActivateTrue(userIds).stream()
        .map(CoffeeChatEntity::getMemberId)
        .collect(Collectors.toSet());
  }

  @Override
  public boolean existsCoffeeChat(Long userId) {
    return coffeeChatJpaRepository.existsByMemberId(userId);
  }
}
