package org.sopt.makers.domain.app.poke.fake;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.sopt.makers.domain.app.poke.Friend;
import org.sopt.makers.domain.app.poke.port.FriendRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class InMemoryFriendRepository implements FriendRepositoryPort {

  private final Map<Long, Friend> store = new LinkedHashMap<>();
  private long sequence = 1L;

  public Friend seed(Long userId, Long friendUserId, int pokeCount) {
    return save(new Friend(null, userId, friendUserId, pokeCount, "익명의 떡볶이"));
  }

  public List<Friend> findAll() {
    return List.copyOf(store.values());
  }

  @Override
  public Optional<Friend> findByUserIdAndFriendUserId(Long userId, Long friendUserId) {
    return store.values().stream()
        .filter(friend -> friend.userId().equals(userId))
        .filter(friend -> friend.friendUserId().equals(friendUserId))
        .findFirst();
  }

  @Override
  public Set<Long> findAllFriendIdsByUserId(Long userId) {
    return store.values().stream()
        .filter(friend -> friend.userId().equals(userId))
        .map(Friend::friendUserId)
        .collect(Collectors.toSet());
  }

  @Override
  public List<Friend> findAllByFriendUserId(Long friendUserId) {
    return store.values().stream()
        .filter(friend -> friend.friendUserId().equals(friendUserId))
        .toList();
  }

  @Override
  public List<Friend> findAllByUserId(Long userId) {
    return store.values().stream().filter(friend -> friend.userId().equals(userId)).toList();
  }

  @Override
  public List<Friend> findAllByUserIdAndFriendUserIdIn(Long userId, List<Long> friendUserIds) {
    return store.values().stream()
        .filter(friend -> friend.userId().equals(userId))
        .filter(friend -> friendUserIds.contains(friend.friendUserId()))
        .toList();
  }

  @Override
  public Page<Friend> findAllByUserIdAndFriendUserIdInOrderByPokeCount(
      Long userId, List<Long> friendUserIds, Pageable pageable) {
    List<Friend> matched =
        findAllByUserIdAndFriendUserIdIn(userId, friendUserIds).stream()
            .sorted(Comparator.comparingInt(Friend::pokeCount))
            .toList();
    int from = (int) Math.min(pageable.getOffset(), matched.size());
    int to = Math.min(from + pageable.getPageSize(), matched.size());
    return new PageImpl<>(matched.subList(from, to), pageable, matched.size());
  }

  @Override
  public Friend save(Friend friend) {
    Long id = friend.id() == null ? sequence++ : friend.id();
    Friend saved =
        new Friend(
            id, friend.userId(), friend.friendUserId(), friend.pokeCount(), friend.anonymousName());
    store.put(id, saved);
    return saved;
  }

  @Override
  public void increasePokeCount(Long friendId) {
    Friend friend = store.get(friendId);
    store.put(
        friendId,
        new Friend(
            friend.id(),
            friend.userId(),
            friend.friendUserId(),
            friend.pokeCount() + 1,
            friend.anonymousName()));
  }
}
