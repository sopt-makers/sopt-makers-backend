package org.sopt.makers.domain.app.poke.fake;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.sopt.makers.domain.app.poke.PokeHistory;
import org.sopt.makers.domain.app.poke.port.PokeHistoryRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class InMemoryPokeHistoryRepository implements PokeHistoryRepositoryPort {

  private static final LocalDateTime BASE_TIME = LocalDateTime.of(2026, 1, 1, 0, 0);

  private final Map<Long, PokeHistory> store = new LinkedHashMap<>();
  private long sequence = 1L;

  public PokeHistory seed(Long pokerId, Long pokedId, boolean isReply) {
    PokeHistory saved = save(PokeHistory.createNew(pokerId, pokedId, "찌르기", false));
    if (isReply) {
      markReplied(saved.id());
    }
    return store.get(saved.id());
  }

  public PokeHistory get(Long id) {
    return store.get(id);
  }

  public List<PokeHistory> findAll() {
    return List.copyOf(store.values());
  }

  @Override
  public Optional<PokeHistory> findById(Long pokeHistoryId) {
    return Optional.ofNullable(store.get(pokeHistoryId));
  }

  @Override
  public PokeHistory save(PokeHistory pokeHistory) {
    Long id = pokeHistory.id() == null ? sequence++ : pokeHistory.id();
    LocalDateTime createdAt =
        pokeHistory.createdAt() == null ? BASE_TIME.plusMinutes(id) : pokeHistory.createdAt();
    PokeHistory saved =
        new PokeHistory(
            id,
            pokeHistory.pokerId(),
            pokeHistory.pokedId(),
            pokeHistory.message(),
            pokeHistory.isReply(),
            pokeHistory.isAnonymous(),
            createdAt,
            createdAt);
    store.put(id, saved);
    return saved;
  }

  @Override
  public void markReplied(Long pokeHistoryId) {
    PokeHistory history = store.get(pokeHistoryId);
    store.put(
        pokeHistoryId,
        new PokeHistory(
            history.id(),
            history.pokerId(),
            history.pokedId(),
            history.message(),
            true,
            history.isAnonymous(),
            history.createdAt(),
            history.updatedAt()));
  }

  @Override
  public List<PokeHistory> findAllUnRepliedFromTo(Long pokerId, Long pokedId) {
    return store.values().stream()
        .filter(history -> isFromTo(history, pokerId, pokedId))
        .filter(history -> !history.isReply())
        .toList();
  }

  @Override
  public List<PokeHistory> findAllByPokedId(Long pokedId) {
    return store.values().stream().filter(history -> history.pokedId().equals(pokedId)).toList();
  }

  @Override
  public List<PokeHistory> findAllByPokerIdAndIsReply(Long pokerId, boolean isReply) {
    return store.values().stream()
        .filter(history -> history.pokerId().equals(pokerId))
        .filter(history -> history.isReply() == isReply)
        .toList();
  }

  @Override
  public List<PokeHistory> findAllFromToOrderByCreatedAtDesc(Long pokerId, Long pokedId) {
    return store.values().stream()
        .filter(history -> isFromTo(history, pokerId, pokedId))
        .sorted(createdAtDesc())
        .toList();
  }

  @Override
  public Page<PokeHistory> findAllByIdInOrderByCreatedAtDesc(
      List<Long> historyIds, Pageable pageable) {
    List<PokeHistory> matched =
        store.values().stream()
            .filter(history -> historyIds.contains(history.id()))
            .sorted(createdAtDesc())
            .toList();
    int from = (int) Math.min(pageable.getOffset(), matched.size());
    int to = Math.min(from + pageable.getPageSize(), matched.size());
    return new PageImpl<>(matched.subList(from, to), pageable, matched.size());
  }

  @Override
  public List<PokeHistory> findAllUnRepliedBetweenOrderByCreatedAtDesc(Long userId, Long friendId) {
    return findAllBetweenOrderByCreatedAtDesc(userId, friendId).stream()
        .filter(history -> !history.isReply())
        .toList();
  }

  @Override
  public List<PokeHistory> findAllBetweenOrderByCreatedAtDesc(Long userId, Long friendId) {
    return store.values().stream()
        .filter(
            history -> isFromTo(history, userId, friendId) || isFromTo(history, friendId, userId))
        .sorted(createdAtDesc())
        .toList();
  }

  @Override
  public long countByPokedIdAndIsReplyFalse(Long pokedId) {
    return store.values().stream()
        .filter(history -> history.pokedId().equals(pokedId))
        .filter(history -> !history.isReply())
        .count();
  }

  @Override
  public long countByPokerId(Long pokerId) {
    return store.values().stream().filter(history -> history.pokerId().equals(pokerId)).count();
  }

  @Override
  public Optional<PokeHistory> findRandomUnRepliedPokeMe(Long userId) {
    return store.values().stream()
        .filter(history -> history.pokedId().equals(userId))
        .filter(history -> !history.isReply())
        .findFirst();
  }

  private boolean isFromTo(PokeHistory history, Long pokerId, Long pokedId) {
    return history.pokerId().equals(pokerId) && history.pokedId().equals(pokedId);
  }

  private Comparator<PokeHistory> createdAtDesc() {
    return Comparator.comparing(PokeHistory::createdAt).reversed();
  }
}
