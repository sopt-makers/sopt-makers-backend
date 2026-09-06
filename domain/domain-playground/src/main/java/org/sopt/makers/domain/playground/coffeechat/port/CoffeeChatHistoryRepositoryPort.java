package org.sopt.makers.domain.playground.coffeechat.port;

public interface CoffeeChatHistoryRepositoryPort {

  void save(Long receiverId, Long senderId, String requestContent);

  long countBySenderId(Long senderId);

  long countByReceiverId(Long receiverId);

  boolean existsByReceiverIdAndSenderId(Long receiverId, Long senderId);
}
