package org.sopt.makers.domain.app.poke.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.sopt.makers.domain.app.poke.exception.PokeFailure.NOT_FOUND_FRIENDSHIP;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.poke.Friend;
import org.sopt.makers.domain.app.poke.Friendship;
import org.sopt.makers.domain.app.poke.Relationship;
import org.sopt.makers.domain.app.poke.exception.PokeException;
import org.sopt.makers.domain.app.poke.fake.InMemoryFriendRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DisplayName("FriendService 테스트")
class FriendServiceTest {

  private static final Long ME = 1L;

  private InMemoryFriendRepository friendRepository;
  private FriendService friendService;

  @BeforeEach
  void setUp() {
    friendRepository = new InMemoryFriendRepository();
    friendService = new FriendService(friendRepository, new AnonymousNameGenerator());
  }

  private void seedFriendships() {
    friendRepository.seed(ME, 2L, 1);
    friendRepository.seed(2L, ME, 1); // 합계 2
    friendRepository.seed(ME, 3L, 1); // 합계 1
    friendRepository.seed(ME, 4L, 3);
    friendRepository.seed(4L, ME, 1); // 합계 4
    friendRepository.seed(ME, 5L, 3);
    friendRepository.seed(5L, ME, 2); // 합계 5
    friendRepository.seed(ME, 6L, 6);
    friendRepository.seed(6L, ME, 4); // 합계 10
    friendRepository.seed(ME, 7L, 6);
    friendRepository.seed(7L, ME, 5); // 합계 11
  }

  private List<Friend> findByFriendship(Friendship friendship) {
    return friendService.findAllFriendsByFriendship(
        ME, friendship.getLowerLimit(), friendship.getUpperLimit());
  }

  @Test
  @DisplayName("친밀도는 내가 찌른 row 와 상대가 찌른 row 의 pokeCount 를 합산한다")
  void sumsPokeCountOfBothDirections() {
    seedFriendships();

    List<Friend> friends = friendService.findAllFriendsByFriendship(ME, 0, Integer.MAX_VALUE);

    assertThat(friends)
        .extracting(Friend::friendUserId, Friend::pokeCount)
        .containsExactlyInAnyOrder(
            tuple(2L, 2), tuple(3L, 1), tuple(4L, 4), tuple(5L, 5), tuple(6L, 10), tuple(7L, 11));
  }

  @Test
  @DisplayName("한쪽 방향 row 만 있어도 그 pokeCount 가 그대로 친밀도가 된다")
  void countsSingleDirectionRow() {
    friendRepository.seed(2L, ME, 3);

    List<Friend> friends = friendService.findAllFriendsByFriendship(ME, 0, Integer.MAX_VALUE);

    assertThat(friends)
        .extracting(Friend::friendUserId, Friend::pokeCount)
        .containsExactly(tuple(2L, 3));
  }

  @Test
  @DisplayName("친밀도 합계 2는 NON_FRIEND 에서 빠지고 NEW_FRIEND 에 들어간다")
  void boundaryOfTwo() {
    seedFriendships();

    assertThat(findByFriendship(Friendship.NON_FRIEND))
        .extracting(Friend::friendUserId)
        .containsExactly(3L);
    assertThat(findByFriendship(Friendship.NEW_FRIEND))
        .extracting(Friend::friendUserId)
        .containsExactly(4L, 2L);
  }

  @Test
  @DisplayName("친밀도 합계 5는 NEW_FRIEND 에서 빠지고 BEST_FRIEND 에 들어간다")
  void boundaryOfFive() {
    seedFriendships();

    assertThat(findByFriendship(Friendship.NEW_FRIEND))
        .extracting(Friend::friendUserId)
        .doesNotContain(5L);
    assertThat(findByFriendship(Friendship.BEST_FRIEND))
        .extracting(Friend::friendUserId)
        .containsExactly(6L, 5L);
  }

  @Test
  @DisplayName("친밀도 합계 11은 BEST_FRIEND 에서 빠지고 SOULMATE 에 들어간다")
  void boundaryOfEleven() {
    seedFriendships();

    assertThat(findByFriendship(Friendship.BEST_FRIEND))
        .extracting(Friend::friendUserId)
        .doesNotContain(7L);
    assertThat(findByFriendship(Friendship.SOULMATE))
        .extracting(Friend::friendUserId)
        .containsExactly(7L);
  }

  @Test
  @DisplayName("친구 목록은 친밀도 내림차순으로 정렬된다")
  void sortsByPokeCountDesc() {
    seedFriendships();

    assertThat(friendService.findAllFriendsByFriendship(ME, 0, Integer.MAX_VALUE))
        .extracting(Friend::friendUserId)
        .containsExactly(7L, 6L, 5L, 4L, 2L, 3L);
  }

  @Test
  @DisplayName("구간별 친구 수와 친밀도 합계도 같은 경계 규칙을 따른다")
  void countsAndSumsByFriendship() {
    seedFriendships();

    assertThat(
            friendService.findAllFriendSizeByFriendship(
                ME, Friendship.BEST_FRIEND.getLowerLimit(), Friendship.BEST_FRIEND.getUpperLimit()))
        .isEqualTo(2);
    assertThat(
            friendService.sumPokeCountByFriendship(
                ME, Friendship.BEST_FRIEND.getLowerLimit(), Friendship.BEST_FRIEND.getUpperLimit()))
        .isEqualTo(15);
  }

  @Test
  @DisplayName("페이지 조회는 구간에 걸러진 친구들의 내 방향 row 를 돌려준다")
  void findAllFriendsByFriendshipWithPaging() {
    seedFriendships();

    Page<Friend> page =
        friendService.findAllFriendsByFriendship(
            ME,
            Friendship.NEW_FRIEND.getLowerLimit(),
            Friendship.NEW_FRIEND.getUpperLimit(),
            PageRequest.of(0, 10));

    assertThat(page.getContent())
        .extracting(Friend::friendUserId, Friend::pokeCount)
        .containsExactly(tuple(2L, 1), tuple(4L, 3));
  }

  @Test
  @DisplayName("registerFriendshipOf 는 찌른 사람 방향 row 하나만 만든다")
  void registerFriendshipCreatesOneDirectionOnly() {
    friendService.registerFriendshipOf(ME, 2L);

    assertThat(friendRepository.findAll()).hasSize(1);
    assertThat(friendRepository.findByUserIdAndFriendUserId(ME, 2L))
        .get()
        .extracting(Friend::pokeCount)
        .isEqualTo(1);
    assertThat(friendRepository.findByUserIdAndFriendUserId(2L, ME)).isEmpty();
    assertThat(friendRepository.findByUserIdAndFriendUserId(ME, 2L).get().anonymousName())
        .startsWith("익명의 ");
  }

  @Test
  @DisplayName("applyPokeCount 는 찌른 사람 방향 row 만 증가시킨다")
  void applyPokeCountIncreasesOneDirectionOnly() {
    friendRepository.seed(ME, 2L, 1);
    friendRepository.seed(2L, ME, 4);

    friendService.applyPokeCount(ME, 2L);

    assertThat(friendRepository.findByUserIdAndFriendUserId(ME, 2L).get().pokeCount()).isEqualTo(2);
    assertThat(friendRepository.findByUserIdAndFriendUserId(2L, ME).get().pokeCount()).isEqualTo(4);
  }

  @Test
  @DisplayName("찌른 사람 방향 row 가 없으면 NOT_FOUND_FRIENDSHIP 예외가 발생한다")
  void applyPokeCountThrowsWhenRowMissing() {
    friendRepository.seed(2L, ME, 4);

    assertThatThrownBy(() -> friendService.applyPokeCount(ME, 2L))
        .isInstanceOf(PokeException.class)
        .extracting("error")
        .isEqualTo(NOT_FOUND_FRIENDSHIP);
  }

  @Test
  @DisplayName("양쪽 방향 row 가 모두 있어야 서로 친구다")
  void isFriendEachOther() {
    friendRepository.seed(ME, 2L, 1);

    assertThat(friendService.isFriendEachOther(ME, 2L)).isFalse();

    friendRepository.seed(2L, ME, 1);

    assertThat(friendService.isFriendEachOther(ME, 2L)).isTrue();
  }

  @Test
  @DisplayName("관계 정보의 익명 이름은 상대가 나를 찌른 방향 row 에서 가져온다")
  void getRelationInfoTakesAnonymousNameFromOppositeRow() {
    friendRepository.save(new Friend(null, ME, 2L, 3, "내가 붙인 이름"));
    friendRepository.save(new Friend(null, 2L, ME, 2, "상대가 붙인 이름"));

    Relationship relationship = friendService.getRelationInfo(ME, 2L);

    assertThat(relationship.pokeNum()).isEqualTo(5);
    assertThat(relationship.relationName()).isEqualTo(Friendship.BEST_FRIEND.getFriendshipName());
    assertThat(relationship.anonymousName()).isEqualTo("상대가 붙인 이름");
  }

  @Test
  @DisplayName("관계가 없으면 pokeNum 0, 익명 이름은 빈 문자열이다")
  void getRelationInfoWhenNoRelation() {
    Relationship relationship = friendService.getRelationInfo(ME, 2L);

    assertThat(relationship.pokeNum()).isZero();
    assertThat(relationship.relationName()).isEqualTo(Friendship.NON_FRIEND.getFriendshipName());
    assertThat(relationship.anonymousName()).isEmpty();
  }
}
