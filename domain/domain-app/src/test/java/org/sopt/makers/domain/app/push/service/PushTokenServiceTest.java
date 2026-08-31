package org.sopt.makers.domain.app.push.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.push.PushMessage;
import org.sopt.makers.domain.app.push.PushToken;
import org.sopt.makers.domain.app.push.PushTokenPlatform;
import org.sopt.makers.domain.app.push.exception.PushException;
import org.sopt.makers.domain.app.push.exception.PushFailure;
import org.sopt.makers.domain.app.push.port.PushSenderPort;
import org.sopt.makers.domain.app.push.port.PushTokenRepositoryPort;

@DisplayName("PushTokenService 테스트")
class PushTokenServiceTest {

  private InMemoryPushTokenRepositoryPort pushTokenRepositoryPort;
  private RecordingPushSenderPort pushSenderPort;
  private PushTokenService pushTokenService;

  @BeforeEach
  void setUp() {
    pushTokenRepositoryPort = new InMemoryPushTokenRepositoryPort();
    pushSenderPort = new RecordingPushSenderPort();
    pushTokenService = new PushTokenService(pushTokenRepositoryPort, pushSenderPort);
  }

  @Test
  @DisplayName("새 토큰은 알림 서버에 등록하고 로컬에도 저장한다")
  void registersNewToken() {
    pushTokenService.register(1L, "device-token", PushTokenPlatform.IOS);

    assertThat(pushSenderPort.registered).hasSize(1);
    assertThat(pushTokenRepositoryPort.findAllByUserId(1L)).hasSize(1);
  }

  @Test
  @DisplayName("이미 등록된 토큰이면 알림 서버를 호출하지 않는다")
  void skipsAlreadyRegisteredToken() {
    pushTokenService.register(1L, "device-token", PushTokenPlatform.IOS);
    pushSenderPort.registered.clear();

    pushTokenService.register(1L, "device-token", PushTokenPlatform.IOS);

    assertThat(pushSenderPort.registered).isEmpty();
    assertThat(pushTokenRepositoryPort.findAllByUserId(1L)).hasSize(1);
  }

  @Test
  @DisplayName("알림 서버 등록이 실패하면 로컬에도 저장하지 않는다")
  void doesNotSaveWhenRegisterFails() {
    pushSenderPort.failOnRegister = true;

    try {
      pushTokenService.register(1L, "device-token", PushTokenPlatform.ANDROID);
    } catch (PushException ignored) {
    }

    assertThat(pushTokenRepositoryPort.findAllByUserId(1L)).isEmpty();
  }

  @Test
  @DisplayName("해제는 로컬에서 지우고 알림 서버에도 알린다")
  void deletesToken() {
    pushTokenService.register(1L, "device-token", PushTokenPlatform.IOS);

    pushTokenService.delete(1L, "device-token");

    assertThat(pushTokenRepositoryPort.findAllByUserId(1L)).isEmpty();
    assertThat(pushSenderPort.deleted).hasSize(1);
  }

  @Test
  @DisplayName("알림 서버 해지가 실패해도 로컬 토큰은 지워진 채로 둔다")
  void keepsLocalDeletionWhenSenderFails() {
    pushTokenService.register(1L, "device-token", PushTokenPlatform.IOS);
    pushSenderPort.failOnDelete = true;

    pushTokenService.delete(1L, "device-token");

    assertThat(pushTokenRepositoryPort.findAllByUserId(1L)).isEmpty();
  }

  @Test
  @DisplayName("없는 토큰을 해제해도 아무 일도 일어나지 않는다")
  void deletingUnknownTokenIsNoop() {
    pushTokenService.delete(1L, "unknown");

    assertThat(pushSenderPort.deleted).isEmpty();
  }

  @Test
  @DisplayName("탈퇴 시 유저의 토큰을 모두 해제한다")
  void deletesAllTokensOfUser() {
    pushTokenService.register(1L, "token-1", PushTokenPlatform.IOS);
    pushTokenService.register(1L, "token-2", PushTokenPlatform.ANDROID);
    pushTokenService.register(2L, "token-3", PushTokenPlatform.IOS);

    pushTokenService.deleteAllByUserId(1L);

    assertThat(pushTokenRepositoryPort.findAllByUserId(1L)).isEmpty();
    assertThat(pushTokenRepositoryPort.findAllByUserId(2L)).hasSize(1);
    assertThat(pushSenderPort.deleted).hasSize(2);
  }

  private static final class InMemoryPushTokenRepositoryPort implements PushTokenRepositoryPort {

    private final List<PushToken> store = new ArrayList<>();
    private long sequence = 1L;

    @Override
    public boolean existsByUserIdAndToken(Long userId, String token) {
      return findByUserIdAndToken(userId, token).isPresent();
    }

    @Override
    public Optional<PushToken> findByUserIdAndToken(Long userId, String token) {
      return store.stream()
          .filter(t -> t.userId().equals(userId) && t.token().equals(token))
          .findFirst();
    }

    @Override
    public List<PushToken> findAllByUserId(Long userId) {
      return store.stream().filter(t -> t.userId().equals(userId)).toList();
    }

    @Override
    public PushToken save(PushToken pushToken) {
      PushToken saved =
          new PushToken(sequence++, pushToken.userId(), pushToken.token(), pushToken.platform());
      store.add(saved);
      return saved;
    }

    @Override
    public void deleteById(Long id) {
      store.removeIf(t -> t.id().equals(id));
    }

    @Override
    public void deleteAllByUserId(Long userId) {
      store.removeIf(t -> t.userId().equals(userId));
    }
  }

  private static final class RecordingPushSenderPort implements PushSenderPort {

    private final List<PushToken> registered = new ArrayList<>();
    private final List<PushToken> deleted = new ArrayList<>();
    private boolean failOnRegister;
    private boolean failOnDelete;

    @Override
    public void send(PushMessage message) {}

    @Override
    public void register(PushToken command) {
      if (failOnRegister) {
        throw new PushException(PushFailure.FAIL_MANAGE_PUSH_TOKEN);
      }
      registered.add(command);
    }

    @Override
    public void delete(PushToken command) {
      if (failOnDelete) {
        throw new PushException(PushFailure.FAIL_MANAGE_PUSH_TOKEN);
      }
      deleted.add(command);
    }
  }
}
