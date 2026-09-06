package org.sopt.makers.domain.playground.member.profile.port;

import java.util.Collection;
import java.util.Set;

public interface CoffeeChatActivationPort {

  boolean isCoffeeChatActive(Long userId);

  Set<Long> findActiveUserIds(Collection<Long> userIds);
}
