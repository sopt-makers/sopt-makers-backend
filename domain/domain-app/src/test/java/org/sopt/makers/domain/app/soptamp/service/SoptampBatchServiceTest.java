package org.sopt.makers.domain.app.soptamp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.core.type.OAuthPlatform;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.app.operationconfig.OperationConfigCategory;
import org.sopt.makers.domain.app.operationconfig.service.OperationConfigService;
import org.sopt.makers.domain.app.soptamp.SoptampMode;
import org.sopt.makers.domain.app.soptamp.SoptampPart;
import org.sopt.makers.domain.app.soptamp.SoptampUser;
import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.sopt.makers.domain.app.soptamp.support.InMemoryAppjamUserStore;
import org.sopt.makers.domain.app.soptamp.support.InMemoryProfileSource;
import org.sopt.makers.domain.app.soptamp.support.InMemorySoptampUserStore;
import org.sopt.makers.domain.app.soptletter.support.InMemoryOperationConfigPort;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.ActivityList;
import org.sopt.makers.domain.user.Profile;
import org.sopt.makers.domain.user.Role;
import org.sopt.makers.domain.user.SocialAccount;
import org.sopt.makers.domain.user.User;

@DisplayName("SoptampBatchService 테스트")
class SoptampBatchServiceTest {

  private static final long CURRENT_GENERATION = 38L;
  private static final int TOTAL_USERS = 250;

  private final InMemorySoptampUserStore store = new InMemorySoptampUserStore(CURRENT_GENERATION);
  private final InMemoryProfileSource profileSource = new InMemoryProfileSource();
  private final InMemoryOperationConfigPort operationConfigPort = new InMemoryOperationConfigPort();
  private final List<Object> published = new ArrayList<>();

  @Test
  @DisplayName("일반 모드는 이미 솝탬프에 있는 유저만 100건씩 끊어 돌린다")
  void upsertsRegisteredUsersInChunksOfHundred() {
    givenUsers(TOTAL_USERS);
    profileSource.save(user(999L, "이솝트"));

    SoptampUpsertResult result = batchService(false).upsertAllSoptampUsers();

    assertThat(store.findAllUserIds()).hasSize(TOTAL_USERS);
    assertThat(store.findByUserId(999L)).isEmpty();
    assertThat(store.findByUserId(1L).orElseThrow().generation()).isEqualTo(CURRENT_GENERATION);
    assertThat(result).isEqualTo(new SoptampUpsertResult(TOTAL_USERS, TOTAL_USERS, 0, 0, 0));
    assertThat(result.isFailed()).isFalse();
  }

  @Test
  @DisplayName("기수가 비어 건너뛴 유저와 프로필이 없어 누락된 유저가 집계에 드러나고 실행은 실패로 잡힌다")
  void reportsSkippedAndMissingUsers() {
    givenUsers(TOTAL_USERS);
    store.save(new SoptampUser(1L, 1L, "", 10L, "서버솝트1", null, SoptampPart.SERVER));
    store.save(new SoptampUser(300L, 300L, "", 10L, "서버솝트300", 37L, SoptampPart.SERVER));

    SoptampUpsertResult result = batchService(false).upsertAllSoptampUsers();

    assertThat(result)
        .isEqualTo(new SoptampUpsertResult(TOTAL_USERS + 1, TOTAL_USERS - 1, 1, 1, 0));
    assertThat(result.isFailed()).isTrue();
    assertThat(store.findByUserId(1L).orElseThrow().generation()).isNull();
    assertThat(store.findByUserId(300L).orElseThrow().generation()).isEqualTo(37L);
    assertThat(store.findByUserId(250L).orElseThrow().generation()).isEqualTo(CURRENT_GENERATION);
  }

  @Test
  @DisplayName("한 청크가 실패해도 나머지 청크는 그대로 돈다")
  void keepsGoingWhenOneChunkFails() {
    givenUsers(TOTAL_USERS);
    profileSource.failChunkContaining(150L);

    SoptampUpsertResult result = batchService(false).upsertAllSoptampUsers();

    assertThat(result).isEqualTo(new SoptampUpsertResult(TOTAL_USERS, TOTAL_USERS - 100, 0, 0, 1));
    assertThat(result.isFailed()).isTrue();
    assertThat(upsertedCount()).isEqualTo(TOTAL_USERS - 100);
    assertThat(store.findByUserId(150L).orElseThrow().generation()).isEqualTo(37L);
    assertThat(store.findByUserId(1L).orElseThrow().generation()).isEqualTo(CURRENT_GENERATION);
    assertThat(store.findByUserId(250L).orElseThrow().generation()).isEqualTo(CURRENT_GENERATION);
  }

  @Test
  @DisplayName("앱잼 모드는 솝탬프에 없던 유저도 새로 만든다")
  void createsUnregisteredUsersInAppjamMode() {
    profileSource.save(user(1L, "김솝트"));

    SoptampUpsertResult result = batchService(true).upsertAllSoptampUsers();

    assertThat(store.findByUserId(1L).orElseThrow().nickname()).isEqualTo("38기김솝트");
    assertThat(result).isEqualTo(new SoptampUpsertResult(1, 1, 0, 0, 0));
    assertThat(result.isFailed()).isFalse();
  }

  @Test
  @DisplayName("cron 은 저장된 값을 읽고 없으면 매일 새벽 3시로 떨어진다")
  void readsUpsertCronFromOperationConfig() {
    assertThat(batchService(false).getUpsertCron()).isEqualTo("0 0 3 * * *");

    new OperationConfigService(operationConfigPort)
        .upsertValue(OperationConfigCategory.SOPTAMP_BATCH, "UPSERT_CRON", "0 30 5 * * *", "설명");

    assertThat(batchService(false).getUpsertCron()).isEqualTo("0 30 5 * * *");
  }

  @Test
  @DisplayName("어드민이 바꾼 cron 은 그대로 저장돼 다음 조회에 보인다")
  void savesUpsertCron() {
    SoptampBatchService service = batchService(false);

    service.setUpsertCron("0 30 5 * * *");

    assertThat(service.getUpsertCron()).isEqualTo("0 30 5 * * *");
  }

  @Test
  @DisplayName("파싱되지 않는 cron 은 저장 전에 막힌다")
  void rejectsUnparsableCron() {
    SoptampBatchService service = batchService(false);

    assertThatThrownBy(() -> service.setUpsertCron("0 3 * * *"))
        .isInstanceOf(SoptampException.class)
        .hasMessage(SoptampFailure.INVALID_UPSERT_CRON.getMessage());
    assertThatThrownBy(() -> service.setUpsertCron(""))
        .isInstanceOf(SoptampException.class)
        .hasMessage(SoptampFailure.INVALID_UPSERT_CRON.getMessage());
    assertThat(service.getUpsertCron()).isEqualTo("0 0 3 * * *");
  }

  private void givenUsers(int count) {
    for (long userId = 1L; userId <= count; userId++) {
      store.save(
          new SoptampUser(userId, userId, "", 10L, "서버솝트" + userId, 37L, SoptampPart.SERVER));
      profileSource.save(user(userId, "솝트" + userId));
    }
  }

  private long upsertedCount() {
    return store.findAllUserIds().stream()
        .map(userId -> store.findByUserId(userId).orElseThrow())
        .filter(user -> CURRENT_GENERATION == user.generation())
        .count();
  }

  private SoptampBatchService batchService(boolean appjamMode) {
    SoptampMode soptampMode = new SoptampMode(appjamMode);
    SoptampUserUpserter upserter =
        new SoptampUserUpserter(
            store,
            store,
            new InMemoryAppjamUserStore(),
            soptampMode,
            published::add,
            CURRENT_GENERATION);
    return new SoptampBatchService(
        upserter,
        store,
        profileSource,
        new OperationConfigService(operationConfigPort),
        soptampMode);
  }

  private static User user(Long userId, String name) {
    return User.createUser(
        userId,
        SocialAccount.of("social-" + userId, OAuthPlatform.APPLE),
        Profile.of(name, null, "01000000000", null),
        ActivityList.of(
            List.of(
                Activity.of(
                    null, (int) CURRENT_GENERATION, null, Part.SERVER, Role.MEMBER, true, null))),
        false);
  }
}
