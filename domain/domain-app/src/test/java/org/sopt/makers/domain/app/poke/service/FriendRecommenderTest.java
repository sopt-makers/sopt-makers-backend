package org.sopt.makers.domain.app.poke.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.poke.FriendRecommendType;
import org.sopt.makers.domain.app.poke.RecommendedFriends;
import org.sopt.makers.domain.app.poke.RecommendedFriendsByType;
import org.sopt.makers.domain.app.poke.fake.InMemoryAppPokeUserPort;
import org.sopt.makers.domain.app.poke.fake.InMemoryFriendRepository;
import org.sopt.makers.domain.user.PokeUserProfile;
import org.sopt.makers.domain.user.RecommendSeed;

@DisplayName("FriendRecommender 테스트")
class FriendRecommenderTest {

  private static final Long ME = 1L;

  private InMemoryFriendRepository friendRepository;
  private InMemoryAppPokeUserPort pokeUserPort;
  private FriendRecommender friendRecommender;

  @BeforeEach
  void setUp() {
    friendRepository = new InMemoryFriendRepository();
    pokeUserPort = new InMemoryAppPokeUserPort();
    friendRecommender =
        new FriendRecommender(
            new FriendService(friendRepository, new AnonymousNameGenerator()), pokeUserPort);
    for (long userId = 1L; userId <= 4L; userId++) {
      pokeUserPort.addUser(userId, "유저" + userId);
    }
    pokeUserPort.addRecommendCandidates(1L, 2L, 3L, 4L);
  }

  private List<Long> recommendedIdsOf(RecommendedFriends recommended, int index) {
    return recommended.randomInfoList().get(index).userInfoList().stream()
        .map(PokeUserProfile::userId)
        .toList();
  }

  @Test
  @DisplayName("활동 이력이 없는 후보는 추천에서 빠진다")
  void excludesCandidatesWithoutActivity() {
    pokeUserPort.addUser(new PokeUserProfile(5L, "유저5", "image-5.png", null, null));
    pokeUserPort.addRecommendCandidates(5L);

    RecommendedFriends recommended =
        friendRecommender.recommendFriendsByTypeList(List.of(FriendRecommendType.ALL_USER), 5, ME);

    assertThat(recommendedIdsOf(recommended, 0)).containsExactlyInAnyOrder(2L, 3L, 4L);
  }

  @Test
  @DisplayName("이미 친구인 유저와 본인은 추천 후보에서 빠진다")
  void excludesFriendsAndSelf() {
    friendRepository.seed(ME, 2L, 1);

    RecommendedFriends recommended =
        friendRecommender.recommendFriendsByTypeList(List.of(FriendRecommendType.ALL_USER), 5, ME);

    assertThat(recommendedIdsOf(recommended, 0)).containsExactlyInAnyOrder(3L, 4L);
  }

  @Test
  @DisplayName("타입을 주지 않으면 전체 유저 중에서 추천한다")
  void defaultsToAllUser() {
    RecommendedFriends recommended = friendRecommender.recommendFriendsByTypeList(List.of(), 5, ME);

    assertThat(recommended.randomInfoList())
        .extracting(RecommendedFriendsByType::randomType)
        .containsExactly(FriendRecommendType.ALL_USER);
    assertThat(recommendedIdsOf(recommended, 0)).containsExactlyInAnyOrder(2L, 3L, 4L);
  }

  @Test
  @DisplayName("MBTI 타입인데 내 MBTI 가 없으면 추천 결과가 비어 있다")
  void skipsMbtiWhenSeedMbtiIsNull() {
    pokeUserPort.setRecommendSeed(ME, new RecommendSeed(null, "솝트대학교", List.of(34)));

    RecommendedFriends recommended =
        friendRecommender.recommendFriendsByTypeList(List.of(FriendRecommendType.MBTI), 5, ME);

    assertThat(recommended.randomInfoList()).isEmpty();
  }

  @Test
  @DisplayName("MBTI 가 있으면 MBTI 타입으로 추천한다")
  void recommendsByMbti() {
    pokeUserPort.setRecommendSeed(ME, new RecommendSeed("INFP", null, List.of(34)));

    RecommendedFriends recommended =
        friendRecommender.recommendFriendsByTypeList(List.of(FriendRecommendType.MBTI), 5, ME);

    assertThat(recommended.randomInfoList()).hasSize(1);
    assertThat(recommended.randomInfoList().getFirst().randomTitle())
        .isEqualTo(FriendRecommendType.MBTI.getRecommendTitle());
    assertThat(recommendedIdsOf(recommended, 0)).containsExactlyInAnyOrder(2L, 3L, 4L);
  }

  @Test
  @DisplayName("ALL 은 기수, MBTI, 학교로 나눠 추천하고 조건이 없는 타입은 빠진다")
  void allTypeSplitsIntoThreeAndSkipsMissingCondition() {
    pokeUserPort.setRecommendSeed(ME, new RecommendSeed(null, "솝트대학교", List.of(34)));

    RecommendedFriends recommended =
        friendRecommender.recommendFriendsByTypeList(List.of(FriendRecommendType.ALL), 5, ME);

    assertThat(recommended.randomInfoList())
        .extracting(RecommendedFriendsByType::randomType)
        .containsExactly(FriendRecommendType.GENERATION, FriendRecommendType.UNIVERSITY);
  }

  @Test
  @DisplayName("추천 인원 수만큼만 뽑는다")
  void picksUpToRequestedSize() {
    RecommendedFriends recommended =
        friendRecommender.recommendFriendsByTypeList(List.of(FriendRecommendType.ALL_USER), 2, ME);

    assertThat(recommendedIdsOf(recommended, 0)).hasSize(2).doesNotContain(ME);
  }

  @Test
  @DisplayName("추천 인원 수가 0 이하면 추천 결과가 비어 있다")
  void returnsEmptyWhenSizeIsNotPositive() {
    RecommendedFriends recommended =
        friendRecommender.recommendFriendsByTypeList(List.of(FriendRecommendType.ALL_USER), 0, ME);

    assertThat(recommended.randomInfoList()).isEmpty();
  }
}
