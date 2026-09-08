package org.sopt.makers.domain.playground.member.profile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.playground.member.profile.port.UserActivityCheckPort;
import org.sopt.makers.domain.playground.member.profile.port.UserProfileCardCachePort;
import org.sopt.makers.domain.playground.member.profile.port.UserProfileNotifierPort;
import org.sopt.makers.domain.playground.member.profile.port.UserProfileRankingCachePort;
import org.sopt.makers.domain.user.ActivityList;
import org.sopt.makers.domain.user.Profile;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.UserLink;
import org.sopt.makers.domain.user.port.PlaygroundProfileUserPort;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/** 프로필/랭킹 캐시 무효화가 트랜잭션 커밋 이전에는 실행되지 않고 afterCommit 시점에만 실행되는지 검증한다. */
class UserProfileCommandServiceTest {

  private static final Long USER_ID = 1L;
  private static final Long LINK_ID = 100L;

  private final PlaygroundProfileUserPort playgroundProfileUserPort =
      mock(PlaygroundProfileUserPort.class);
  private final UserProfileRankingCachePort rankingCachePort =
      mock(UserProfileRankingCachePort.class);
  private final UserProfileCardCachePort cardCachePort = mock(UserProfileCardCachePort.class);
  private final UserActivityCheckPort userActivityCheckPort = mock(UserActivityCheckPort.class);
  private final UserProfileNotifierPort userProfileNotifierPort =
      mock(UserProfileNotifierPort.class);

  private final UserProfileCommandService service =
      new UserProfileCommandService(
          playgroundProfileUserPort,
          rankingCachePort,
          cardCachePort,
          userActivityCheckPort,
          userProfileNotifierPort);

  @AfterEach
  void tearDown() {
    if (TransactionSynchronizationManager.isSynchronizationActive()) {
      TransactionSynchronizationManager.clearSynchronization();
    }
  }

  @Test
  @DisplayName("트랜잭션 동기화가 활성화된 상태에서 링크를 삭제하면 랭킹/카드 캐시 무효화는 afterCommit 이전에는 실행되지 않는다")
  void deleteLinkDoesNotEvictBeforeCommit() {
    TransactionSynchronizationManager.initSynchronization();
    when(playgroundProfileUserPort.findLinkById(LINK_ID))
        .thenReturn(Optional.of(UserLink.of(LINK_ID, USER_ID, "title", "url")));

    service.deleteLink(USER_ID, LINK_ID);

    verify(rankingCachePort, never()).evictTopRanking();
    verify(cardCachePort, never()).evict(USER_ID);
    assertThat(TransactionSynchronizationManager.getSynchronizations()).hasSize(1);
  }

  @Test
  @DisplayName("afterCommit이 호출되면 그제서야 랭킹/카드 캐시가 모두 무효화된다")
  void deleteLinkEvictsOnlyAfterCommitFires() {
    TransactionSynchronizationManager.initSynchronization();
    when(playgroundProfileUserPort.findLinkById(LINK_ID))
        .thenReturn(Optional.of(UserLink.of(LINK_ID, USER_ID, "title", "url")));

    service.deleteLink(USER_ID, LINK_ID);
    for (TransactionSynchronization synchronization :
        TransactionSynchronizationManager.getSynchronizations()) {
      synchronization.afterCommit();
    }

    verify(rankingCachePort).evictTopRanking();
    verify(cardCachePort).evict(USER_ID);
  }

  @Test
  @DisplayName("활성화된 트랜잭션 동기화가 없으면 캐시 무효화를 즉시 실행한다")
  void deleteLinkEvictsImmediatelyWithoutActiveSynchronization() {
    assertThat(TransactionSynchronizationManager.isSynchronizationActive()).isFalse();
    when(playgroundProfileUserPort.findLinkById(LINK_ID))
        .thenReturn(Optional.of(UserLink.of(LINK_ID, USER_ID, "title", "url")));

    service.deleteLink(USER_ID, LINK_ID);

    verify(rankingCachePort).evictTopRanking();
    verify(cardCachePort).evict(USER_ID);
  }

  @Test
  @DisplayName("프로필 수정 시 랭킹/카드 캐시 무효화는 afterCommit 전까지 실행되지 않고, afterCommit 이후에 모두 실행된다")
  void updateProfileDefersRankingAndCardCacheEvictionUntilAfterCommit() {
    TransactionSynchronizationManager.initSynchronization();
    User user = user();
    when(playgroundProfileUserPort.getUserWithActivities(USER_ID)).thenReturn(user);
    when(playgroundProfileUserPort.getUser(USER_ID)).thenReturn(user);

    service.updateProfile(
        USER_ID,
        "email@test.com",
        "010-0000-0000",
        null,
        List.of(),
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        List.of(),
        List.of(),
        null,
        null);

    verify(rankingCachePort, never()).evictTopRanking();
    verify(cardCachePort, never()).evict(USER_ID);

    for (TransactionSynchronization synchronization :
        TransactionSynchronizationManager.getSynchronizations()) {
      synchronization.afterCommit();
    }

    verify(rankingCachePort).evictTopRanking();
    verify(cardCachePort).evict(USER_ID);
  }

  private User user() {
    Profile profile = Profile.of("이름", "email@test.com", "010-0000-0000", null);
    return new User(USER_ID, profile, null, new ActivityList(), false);
  }
}
