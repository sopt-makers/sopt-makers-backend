package org.sopt.makers.domain.app.soptletter.port;

public interface SoptLetterLikeNotificationRepositoryPort {

  int addIfAbsent(Long userId, Long letterId);
}
