package org.sopt.makers.domain.app.push.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.domain.app.push.PushToken;
import org.sopt.makers.domain.app.push.PushTokenPlatform;
import org.sopt.makers.domain.app.push.exception.PushException;
import org.sopt.makers.domain.app.push.port.PushSenderPort;
import org.sopt.makers.domain.app.push.port.PushTokenRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PushTokenService {

  private final PushTokenRepositoryPort pushTokenRepositoryPort;
  private final PushSenderPort pushSenderPort;

  @Transactional
  public void register(Long userId, String token, PushTokenPlatform platform) {
    if (pushTokenRepositoryPort.existsByUserIdAndToken(userId, token)) {
      return;
    }
    PushToken pushToken = PushToken.create(userId, token, platform);
    pushSenderPort.register(pushToken);
    pushTokenRepositoryPort.save(pushToken);
  }

  @Transactional
  public void delete(Long userId, String token) {
    pushTokenRepositoryPort.findByUserIdAndToken(userId, token).ifPresent(this::deleteToken);
  }

  @Transactional
  public void deleteAllByUserId(Long userId) {
    List<PushToken> tokens = pushTokenRepositoryPort.findAllByUserId(userId);
    tokens.forEach(this::notifySenderOfDeletion);
    pushTokenRepositoryPort.deleteAllByUserId(userId);
  }

  private void deleteToken(PushToken pushToken) {
    pushTokenRepositoryPort.deleteById(pushToken.id());
    notifySenderOfDeletion(pushToken);
  }

  private void notifySenderOfDeletion(PushToken pushToken) {
    try {
      pushSenderPort.delete(pushToken);
    } catch (PushException e) {
      log.warn("알림 서버 푸시 토큰 해지 실패 - userId={}", pushToken.userId(), e);
    }
  }
}
