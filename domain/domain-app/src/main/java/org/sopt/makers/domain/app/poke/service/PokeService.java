package org.sopt.makers.domain.app.poke.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.poke.PokeDetail;
import org.sopt.makers.domain.app.poke.PokeEvent;
import org.sopt.makers.domain.app.poke.PokeHistory;
import org.sopt.makers.domain.app.poke.exception.PokeException;
import org.sopt.makers.domain.app.poke.exception.PokeFailure;
import org.sopt.makers.domain.app.poke.port.PokeHistoryRepositoryPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PokeService {

  private final PokeHistoryRepositoryPort pokeHistoryRepositoryPort;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional(readOnly = true)
  public PokeDetail getPokeDetail(Long pokeHistoryId) {
    PokeHistory latestPokeHistory =
        pokeHistoryRepositoryPort
            .findById(pokeHistoryId)
            .orElseThrow(() -> new PokeException(PokeFailure.NOT_FOUND_POKE_HISTORY));
    return new PokeDetail(
        latestPokeHistory.id(),
        latestPokeHistory.pokerId(),
        latestPokeHistory.pokedId(),
        latestPokeHistory.message());
  }

  @Transactional
  public PokeHistory poke(
      Long pokerUserId, Long pokedUserId, String pokeMessage, boolean isAnonymous) {
    PokeHistory saved =
        createPokeByApplyingReply(pokerUserId, pokedUserId, pokeMessage, isAnonymous);
    eventPublisher.publishEvent(new PokeEvent(pokedUserId));
    return saved;
  }

  private PokeHistory createPokeByApplyingReply(
      Long pokerUserId, Long pokedUserId, String pokeMessage, boolean isAnonymous) {
    List<PokeHistory> unReplied =
        pokeHistoryRepositoryPort.findAllUnRepliedFromTo(pokedUserId, pokerUserId);
    if (!unReplied.isEmpty()) {
      pokeHistoryRepositoryPort.markReplied(unReplied.getFirst().id());
    }
    return pokeHistoryRepositoryPort.save(
        PokeHistory.createNew(pokerUserId, pokedUserId, pokeMessage, isAnonymous));
  }

  @Transactional(readOnly = true)
  public Long getUserPokeCount(Long userId) {
    return pokeHistoryRepositoryPort.countByPokerId(userId);
  }
}
