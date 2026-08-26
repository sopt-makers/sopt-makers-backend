package org.sopt.makers.domain.app.poke.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.sopt.makers.domain.app.poke.exception.PokeFailure.DUPLICATE_POKE;
import static org.sopt.makers.domain.app.poke.exception.PokeFailure.NOT_FOUND_USER;
import static org.sopt.makers.domain.app.poke.exception.PokeFailure.SELF_POKE_NOT_ALLOWED;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.poke.EachRelationFriendListData;
import org.sopt.makers.domain.app.poke.Friend;
import org.sopt.makers.domain.app.poke.Friendship;
import org.sopt.makers.domain.app.poke.PokeEvent;
import org.sopt.makers.domain.app.poke.PokeHistory;
import org.sopt.makers.domain.app.poke.PokeToMeHistoryData;
import org.sopt.makers.domain.app.poke.exception.PokeException;
import org.sopt.makers.domain.app.poke.fake.InMemoryAppPokeUserPort;
import org.sopt.makers.domain.app.poke.fake.InMemoryFriendRepository;
import org.sopt.makers.domain.app.poke.fake.InMemoryPokeHistoryRepository;
import org.sopt.makers.domain.app.poke.service.AnonymousNameGenerator;
import org.sopt.makers.domain.app.poke.service.FriendRecommender;
import org.sopt.makers.domain.app.poke.service.FriendService;
import org.sopt.makers.domain.app.poke.service.PokeHistoryService;
import org.sopt.makers.domain.app.poke.service.PokeMessageService;
import org.sopt.makers.domain.app.poke.service.PokeService;
import org.springframework.data.domain.PageRequest;

@DisplayName("PokeFacade.pokeFriend 테스트")
class PokeFacadeTest {

  private static final Long POKER = 1L;
  private static final Long POKED = 2L;

  private InMemoryFriendRepository friendRepository;
  private InMemoryPokeHistoryRepository pokeHistoryRepository;
  private InMemoryAppPokeUserPort pokeUserPort;
  private List<Object> publishedEvents;
  private PokeFacade pokeFacade;

  @BeforeEach
  void setUp() {
    friendRepository = new InMemoryFriendRepository();
    pokeHistoryRepository = new InMemoryPokeHistoryRepository();
    pokeUserPort = new InMemoryAppPokeUserPort();
    pokeUserPort.addUser(POKER, "찌른 사람");
    pokeUserPort.addUser(POKED, "찔린 사람");
    publishedEvents = new ArrayList<>();

    FriendService friendService = new FriendService(friendRepository, new AnonymousNameGenerator());
    pokeFacade =
        new PokeFacade(
            friendService,
            new FriendRecommender(friendService, pokeUserPort),
            new PokeService(pokeHistoryRepository, publishedEvents::add),
            new PokeHistoryService(pokeHistoryRepository),
            new PokeMessageService(type -> List.of()),
            pokeUserPort);
  }

  @Test
  @DisplayName("자기 자신을 찌르면 SELF_POKE_NOT_ALLOWED 예외가 발생한다")
  void selfPokeIsNotAllowed() {
    assertThatThrownBy(() -> pokeFacade.pokeFriend(POKER, POKER, "안녕", false))
        .isInstanceOf(PokeException.class)
        .extracting("error")
        .isEqualTo(SELF_POKE_NOT_ALLOWED);
    assertThat(pokeHistoryRepository.findAll()).isEmpty();
  }

  @Test
  @DisplayName("존재하지 않는 유저를 찌르면 NOT_FOUND_USER 예외가 발생한다")
  void pokeUnknownUser() {
    assertThatThrownBy(() -> pokeFacade.pokeFriend(POKER, 999L, "안녕", false))
        .isInstanceOf(PokeException.class)
        .extracting("error")
        .isEqualTo(NOT_FOUND_USER);
    assertThat(pokeHistoryRepository.findAll()).isEmpty();
  }

  @Test
  @DisplayName("사라진 유저에게 보낸 미답장 기록이 남아 있어도 DUPLICATE_POKE 가 아니라 NOT_FOUND_USER 가 발생한다")
  void unknownUserBeatsDuplicate() {
    pokeHistoryRepository.seed(POKER, 999L, false);

    assertThatThrownBy(() -> pokeFacade.pokeFriend(POKER, 999L, "안녕", false))
        .isInstanceOf(PokeException.class)
        .extracting("error")
        .isEqualTo(NOT_FOUND_USER);
  }

  @Test
  @DisplayName("나를 찌른 목록의 totalPageSize 는 전체 페이지 수다")
  void pokeMeHistoryTotalPageSize() {
    for (long poker = 100L; poker < 103L; poker++) {
      pokeUserPort.addUser(poker, "유저" + poker);
      pokeHistoryRepository.seed(poker, POKER, false);
    }

    PokeToMeHistoryData page = pokeFacade.getAllPokeMeHistory(POKER, PageRequest.of(0, 2));

    assertThat(page.totalPageSize()).isEqualTo(2);
    assertThat(page.pageSize()).isEqualTo(2);
  }

  @Test
  @DisplayName("친구 목록의 totalPageSize 는 마지막 조각 페이지까지 올림으로 센다")
  void friendListTotalPageSizeCeils() {
    for (long friendId = 200L; friendId < 203L; friendId++) {
      pokeUserPort.addUser(friendId, "친구" + friendId);
      friendRepository.seed(POKER, friendId, 3);
      friendRepository.seed(friendId, POKER, 0);
    }

    EachRelationFriendListData page =
        pokeFacade.getAllFriendByFriendship(POKER, Friendship.NEW_FRIEND, PageRequest.of(0, 2));

    assertThat(page.totalSize()).isEqualTo(3);
    assertThat(page.totalPageSize()).isEqualTo(2);
  }

  @Test
  @DisplayName("내가 보낸 미답장 기록이 남아 있으면 DUPLICATE_POKE 예외가 발생한다")
  void duplicatePoke() {
    pokeHistoryRepository.seed(POKER, POKED, false);

    assertThatThrownBy(() -> pokeFacade.pokeFriend(POKER, POKED, "안녕", false))
        .isInstanceOf(PokeException.class)
        .extracting("error")
        .isEqualTo(DUPLICATE_POKE);
    assertThat(pokeHistoryRepository.findAll()).hasSize(1);
  }

  @Test
  @DisplayName("처음 찌르면 기록을 저장하고 찌른 방향 friend row 하나를 만든다")
  void pokeStrangerSavesHistoryAndRegistersFriendship() {
    Long pokeId = pokeFacade.pokeFriend(POKER, POKED, "안녕", true);

    assertThat(pokeHistoryRepository.findAll())
        .extracting(
            PokeHistory::id, PokeHistory::pokerId, PokeHistory::pokedId, PokeHistory::message)
        .containsExactly(tuple(pokeId, POKER, POKED, "안녕"));
    assertThat(pokeHistoryRepository.get(pokeId).isAnonymous()).isTrue();
    assertThat(publishedEvents).containsExactly(new PokeEvent(POKED));
    assertThat(friendRepository.findAll())
        .extracting(Friend::userId, Friend::friendUserId, Friend::pokeCount)
        .containsExactly(tuple(POKER, POKED, 1));
  }

  @Test
  @DisplayName("상대가 나를 찌른 미답장 기록은 답장 처리하고 반대 방향 friend row 를 새로 만든다")
  void pokeBackMarksRepliedAndRegistersFriendship() {
    PokeHistory pokedMe = pokeHistoryRepository.seed(POKED, POKER, false);
    friendRepository.seed(POKED, POKER, 1);

    pokeFacade.pokeFriend(POKER, POKED, "답장", false);

    assertThat(pokeHistoryRepository.get(pokedMe.id()).isReply()).isTrue();
    assertThat(friendRepository.findAll())
        .extracting(Friend::userId, Friend::friendUserId, Friend::pokeCount)
        .containsExactlyInAnyOrder(tuple(POKED, POKER, 1), tuple(POKER, POKED, 1));
  }

  @Test
  @DisplayName("이미 서로 친구면 새 row 없이 찌른 방향 pokeCount 만 올린다")
  void pokeMutualFriendIncreasesPokeCount() {
    friendRepository.seed(POKER, POKED, 3);
    friendRepository.seed(POKED, POKER, 1);

    pokeFacade.pokeFriend(POKER, POKED, "또 안녕", false);

    assertThat(friendRepository.findAll())
        .extracting(Friend::userId, Friend::friendUserId, Friend::pokeCount)
        .containsExactlyInAnyOrder(tuple(POKER, POKED, 4), tuple(POKED, POKER, 1));
  }

  @Test
  @DisplayName("답장한 기록만 남아 있으면 다시 찌를 수 있다")
  void pokeAgainAfterReplied() {
    friendRepository.seed(POKER, POKED, 1);
    friendRepository.seed(POKED, POKER, 1);
    pokeHistoryRepository.seed(POKER, POKED, true);

    Long pokeId = pokeFacade.pokeFriend(POKER, POKED, "또 안녕", false);

    assertThat(pokeId).isNotNull();
    assertThat(pokeHistoryRepository.findAll()).hasSize(2);
  }
}
