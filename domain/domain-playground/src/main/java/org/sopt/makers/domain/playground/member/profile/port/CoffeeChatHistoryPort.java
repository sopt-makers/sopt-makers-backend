package org.sopt.makers.domain.playground.member.profile.port;

public interface CoffeeChatHistoryPort {

  long countReceivedBy(Long userId);

  long countSentBy(Long userId);
}
