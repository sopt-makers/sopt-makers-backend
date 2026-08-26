package org.sopt.makers.domain.app.poke.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.poke.FriendRecommendType;
import org.sopt.makers.domain.app.poke.RecommendedFriends;
import org.sopt.makers.domain.app.poke.RecommendedFriendsByType;
import org.sopt.makers.domain.user.PokeUserProfile;
import org.sopt.makers.domain.user.RecommendSeed;
import org.sopt.makers.domain.user.port.AppPokeUserPort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendRecommender {

  private static final int MAX_OVERSAMPLE = 50;

  private final FriendService friendService;
  private final AppPokeUserPort pokeUserPort;

  public RecommendedFriends recommendFriendsByTypeList(
      List<FriendRecommendType> typeList, int size, Long userId) {
    List<FriendRecommendType> types = adjustTypeList(typeList);
    RecommendSeed seed = pokeUserPort.findRecommendSeed(userId);
    Set<Long> friendIds = friendService.findAllFriendIdsByUserId(userId);

    List<RecommendedFriendsByType> recommendedFriends = new ArrayList<>();
    for (FriendRecommendType type : types) {
      List<PokeUserProfile> recommendable =
          getRecommendableUserProfiles(type, seed, friendIds, userId, size);
      if (!recommendable.isEmpty()) {
        recommendedFriends.add(
            new RecommendedFriendsByType(
                type, type.getRecommendTitle(), RandomPicker.pickRandom(recommendable, size)));
      }
    }
    return new RecommendedFriends(recommendedFriends);
  }

  private List<PokeUserProfile> getRecommendableUserProfiles(
      FriendRecommendType type, RecommendSeed seed, Set<Long> friendIds, Long userId, int size) {
    // size 가드 : 이상값 들어오면 500 대신 빈 리스트
    if (size <= 0) {
      return List.of();
    }

    Set<Long> userIds =
        type == FriendRecommendType.ALL_USER
            ? Set.copyOf(pokeUserPort.findAllUserIds())
            : findCandidateIdsByType(type, seed);
    if (userIds.isEmpty()) {
      return List.of();
    }

    List<Long> candidates = new ArrayList<>(userIds);

    int oversampleCount = Math.min(MAX_OVERSAMPLE, size * 5);
    oversampleCount = Math.min(oversampleCount, candidates.size());

    List<Long> toLookup =
        candidates.size() <= oversampleCount
            ? candidates
            : RandomPicker.pickRandom(candidates, oversampleCount);

    return pokeUserPort.findProfiles(toLookup).stream()
        .filter(profile -> profile.generation() != null)
        .filter(profile -> !userId.equals(profile.userId()))
        .filter(profile -> !friendIds.contains(profile.userId()))
        .toList();
  }

  private Set<Long> findCandidateIdsByType(FriendRecommendType type, RecommendSeed seed) {
    Set<Integer> generations = Set.copyOf(seed.generations());
    return switch (type) {
      case GENERATION ->
          generations.isEmpty()
              ? Set.of()
              : pokeUserPort.findUserIdsByRecommendCondition(generations, null, null);
      case MBTI ->
          seed.mbti() == null
              ? Set.of()
              : pokeUserPort.findUserIdsByRecommendCondition(generations, seed.mbti(), null);
      case UNIVERSITY ->
          seed.university() == null
              ? Set.of()
              : pokeUserPort.findUserIdsByRecommendCondition(generations, null, seed.university());
      default -> Set.of();
    };
  }

  private List<FriendRecommendType> adjustTypeList(List<FriendRecommendType> typeList) {
    if (typeList == null || typeList.isEmpty()) {
      return List.of(FriendRecommendType.ALL_USER);
    }
    if (typeList.contains(FriendRecommendType.ALL)) {
      return List.of(
          FriendRecommendType.GENERATION, FriendRecommendType.MBTI, FriendRecommendType.UNIVERSITY);
    }
    return typeList;
  }
}
