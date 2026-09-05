package org.sopt.makers.domain.app.poke.fake;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.sopt.makers.domain.app.poke.exception.PokeException;
import org.sopt.makers.domain.app.poke.exception.PokeFailure;
import org.sopt.makers.domain.user.PokeUserProfile;
import org.sopt.makers.domain.user.RecommendSeed;
import org.sopt.makers.domain.user.port.AppPokeUserPort;

public class InMemoryAppPokeUserPort implements AppPokeUserPort {

  private final Map<Long, PokeUserProfile> profiles = new LinkedHashMap<>();
  private final Map<Long, RecommendSeed> seeds = new HashMap<>();
  private final Set<Long> recommendCandidates = new LinkedHashSet<>();

  public void addUser(Long userId, String name) {
    profiles.put(userId, new PokeUserProfile(userId, name, "image-" + userId + ".png", 34L, "서버"));
  }

  public void addUser(PokeUserProfile profile) {
    profiles.put(profile.userId(), profile);
  }

  public void setRecommendSeed(Long userId, RecommendSeed seed) {
    seeds.put(userId, seed);
  }

  public void addRecommendCandidates(Long... userIds) {
    recommendCandidates.addAll(List.of(userIds));
  }

  @Override
  public boolean exists(Long userId) {
    return profiles.containsKey(userId);
  }

  @Override
  public List<Long> filterExisting(Collection<Long> userIds) {
    return userIds.stream().filter(profiles::containsKey).toList();
  }

  @Override
  public List<Long> findAllUserIds() {
    return new ArrayList<>(profiles.keySet());
  }

  @Override
  public PokeUserProfile findProfile(Long userId) {
    return Optional.ofNullable(profiles.get(userId))
        .orElseThrow(() -> new PokeException(PokeFailure.NOT_FOUND_USER));
  }

  @Override
  public List<PokeUserProfile> findProfiles(Collection<Long> userIds) {
    return userIds.stream().map(profiles::get).filter(Objects::nonNull).toList();
  }

  @Override
  public RecommendSeed findRecommendSeed(Long userId) {
    return seeds.getOrDefault(userId, new RecommendSeed(null, null, List.of()));
  }

  @Override
  public Set<Long> findUserIdsByRecommendCondition(
      Set<Integer> generations, String mbti, String university) {
    return Set.copyOf(recommendCandidates);
  }
}
