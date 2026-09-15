package org.sopt.makers.domain.app.poke.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.poke.PokeHistory;
import org.sopt.makers.domain.app.poke.exception.PokeException;
import org.sopt.makers.domain.app.poke.exception.PokeFailure;
import org.sopt.makers.domain.app.poke.port.PokeHistoryRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PokeHistoryService {

  private final PokeHistoryRepositoryPort pokeHistoryRepositoryPort;

  public List<PokeHistory> getAllOfPokeBetween(Long userId, Long friendId) {
    return pokeHistoryRepositoryPort.findAllUnRepliedBetweenOrderByCreatedAtDesc(userId, friendId);
  }

  public Optional<PokeHistory> getRandomUnRepliedPokeMeHistory(Long userId) {
    return pokeHistoryRepositoryPort.findRandomUnRepliedPokeMe(userId);
  }

  public List<Long> getPokeMeUserIds(Long userId) {
    return pokeHistoryRepositoryPort.findAllByPokedId(userId).stream()
        .map(PokeHistory::pokerId)
        .distinct()
        .toList();
  }

  public List<PokeHistory> getAllLatestPokeHistoryFromTo(Long pokerId, Long pokedId) {
    return pokeHistoryRepositoryPort.findAllFromToOrderByCreatedAtDesc(pokerId, pokedId);
  }

  public Page<PokeHistory> getAllLatestPokeHistoryIn(
      List<Long> targetHistoryIds, Pageable pageable) {
    if (targetHistoryIds == null || targetHistoryIds.isEmpty()) {
      return Page.empty(pageable);
    }
    return pokeHistoryRepositoryPort.findAllByIdInOrderByCreatedAtDesc(targetHistoryIds, pageable);
  }

  public void checkDuplicate(Long pokerUserId, Long pokedUserId) {
    if (!pokeHistoryRepositoryPort.findAllUnRepliedFromTo(pokerUserId, pokedUserId).isEmpty()) {
      throw new PokeException(PokeFailure.DUPLICATE_POKE);
    }
  }

  public Map<Long, Boolean> getAllPokeHistoryMap(Long userId) {
    Map<Long, Boolean> pokeHistoryMap = new HashMap<>();
    for (PokeHistory pokeHistory :
        pokeHistoryRepositoryPort.findAllByPokerIdAndIsReply(userId, false)) {
      pokeHistoryMap.put(pokeHistory.pokedId(), pokeHistory.isReply());
    }
    return pokeHistoryMap;
  }

  public List<PokeHistory> getAllPokeHistoryByUsers(Long userId, Long friendUserId) {
    return pokeHistoryRepositoryPort.findAllBetweenOrderByCreatedAtDesc(userId, friendUserId);
  }

  public Long getUnRepliedPokeMeSize(Long userId) {
    return pokeHistoryRepositoryPort.countByPokedIdAndIsReplyFalse(userId);
  }
}
