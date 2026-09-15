package org.sopt.makers.domain.app.poke.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.poke.Friend;
import org.sopt.makers.domain.app.poke.Friendship;
import org.sopt.makers.domain.app.poke.Relationship;
import org.sopt.makers.domain.app.poke.exception.PokeException;
import org.sopt.makers.domain.app.poke.exception.PokeFailure;
import org.sopt.makers.domain.app.poke.port.FriendRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FriendService {

  private final FriendRepositoryPort friendRepositoryPort;
  private final AnonymousNameGenerator anonymousNameGenerator;

  @Transactional(readOnly = true)
  public List<Friend> findAllFriendsByFriendship(Long userId, int lowerLimit, int upperLimit) {
    return getPokeCountMap(userId).entrySet().stream()
        .filter(entry -> entry.getValue() >= lowerLimit && entry.getValue() < upperLimit)
        .sorted(Entry.<Long, Integer>comparingByValue().reversed().thenComparing(Entry::getKey))
        .map(entry -> Friend.ofPokeCount(userId, entry.getKey(), entry.getValue()))
        .toList();
  }

  @Transactional(readOnly = true)
  public int findAllFriendSizeByFriendship(Long userId, int lowerLimit, int upperLimit) {
    return (int)
        getPokeCountMap(userId).values().stream()
            .filter(count -> count >= lowerLimit && count < upperLimit)
            .count();
  }

  @Transactional(readOnly = true)
  public Page<Friend> findAllFriendsByFriendship(
      Long userId, int lowerLimit, int upperLimit, Pageable pageable) {
    List<Friend> friends = findAllFriendsByFriendship(userId, lowerLimit, upperLimit);

    int from = (int) Math.min(pageable.getOffset(), friends.size());
    int to = Math.min(from + pageable.getPageSize(), friends.size());
    return new PageImpl<>(friends.subList(from, to), pageable, friends.size());
  }

  private Map<Long, Integer> getPokeCountMap(Long userId) {
    Map<Long, Integer> map = new HashMap<>();

    for (Friend friend : friendRepositoryPort.findAllByFriendUserId(userId)) {
      map.put(friend.userId(), friend.pokeCount());
    }
    for (Friend friend : friendRepositoryPort.findAllByUserId(userId)) {
      map.merge(friend.friendUserId(), friend.pokeCount(), Integer::sum);
    }
    return map;
  }

  @Transactional
  public void registerFriendshipOf(Long userId, Long friendId) {
    friendRepositoryPort.save(
        Friend.createNew(userId, friendId, anonymousNameGenerator.generate()));
  }

  @Transactional
  public void applyPokeCount(Long pokerId, Long pokedId) {
    Friend friendship =
        friendRepositoryPort
            .findByUserIdAndFriendUserId(pokerId, pokedId)
            .orElseThrow(() -> new PokeException(PokeFailure.NOT_FOUND_FRIENDSHIP));
    friendRepositoryPort.increasePokeCount(friendship.id());
  }

  @Transactional(readOnly = true)
  public boolean isFriendEachOther(Long pokerId, Long pokedId) {
    return friendRepositoryPort.findByUserIdAndFriendUserId(pokerId, pokedId).isPresent()
        && friendRepositoryPort.findByUserIdAndFriendUserId(pokedId, pokerId).isPresent();
  }

  @Transactional(readOnly = true)
  public Relationship getRelationInfo(Long pokerId, Long pokedId) {
    Optional<Friend> friendshipFromPokerToPoked =
        friendRepositoryPort.findByUserIdAndFriendUserId(pokerId, pokedId);
    Optional<Friend> friendshipFromPokedToPoker =
        friendRepositoryPort.findByUserIdAndFriendUserId(pokedId, pokerId);

    String anonymousName = "";
    int totalPokeNum = 0;
    if (friendshipFromPokerToPoked.isPresent()) {
      totalPokeNum += friendshipFromPokerToPoked.get().pokeCount();
    }
    if (friendshipFromPokedToPoker.isPresent()) {
      totalPokeNum += friendshipFromPokedToPoker.get().pokeCount();
      anonymousName = friendshipFromPokedToPoker.get().anonymousName();
    }
    return new Relationship(
        totalPokeNum, Friendship.decideRelationName(totalPokeNum), anonymousName);
  }

  @Transactional(readOnly = true)
  public List<Long> getMutualFriendIds(Long pokerId, Long pokedId) {
    Set<Long> pokerFriendIds = friendRepositoryPort.findAllFriendIdsByUserId(pokerId);
    Set<Long> pokedFriendIds = friendRepositoryPort.findAllFriendIdsByUserId(pokedId);
    return pokerFriendIds.stream().filter(pokedFriendIds::contains).toList();
  }

  @Transactional(readOnly = true)
  public Long getPokeFriendIdRandomly(Long userId) {
    List<Long> friendIdsPokeMe =
        friendRepositoryPort.findAllByFriendUserId(userId).stream().map(Friend::userId).toList();
    List<Friend> friends =
        friendRepositoryPort.findAllByUserIdAndFriendUserIdIn(userId, friendIdsPokeMe);

    if (friendIdsPokeMe.isEmpty() || friends.isEmpty()) {
      throw new PokeException(PokeFailure.NOT_FOUND_FRIENDSHIP);
    }
    return friends.get(ThreadLocalRandom.current().nextInt(friends.size())).friendUserId();
  }

  @Transactional(readOnly = true)
  public boolean getIsNewUser(Long userId) {
    List<Long> friendUserPokeMe =
        friendRepositoryPort.findAllByFriendUserId(userId).stream().map(Friend::userId).toList();
    return friendRepositoryPort
        .findAllByUserIdAndFriendUserIdIn(userId, friendUserPokeMe)
        .isEmpty();
  }

  public Set<Long> findAllFriendIdsByUserId(Long userId) {
    return friendRepositoryPort.findAllFriendIdsByUserId(userId);
  }

  @Transactional(readOnly = true)
  public int sumPokeCountByFriendship(Long userId, int lowerLimit, int upperLimit) {
    return getPokeCountMap(userId).values().stream()
        .filter(count -> count >= lowerLimit && count < upperLimit)
        .mapToInt(Integer::intValue)
        .sum();
  }
}
