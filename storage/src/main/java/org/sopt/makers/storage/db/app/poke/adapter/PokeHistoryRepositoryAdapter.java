package org.sopt.makers.storage.db.app.poke.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.poke.PokeHistory;
import org.sopt.makers.domain.app.poke.port.PokeHistoryRepositoryPort;
import org.sopt.makers.storage.db.app.poke.entity.PokeHistoryEntity;
import org.sopt.makers.storage.db.app.poke.querydsl.PokeHistoryQuerydslRepository;
import org.sopt.makers.storage.db.app.poke.repository.PokeHistoryJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PokeHistoryRepositoryAdapter implements PokeHistoryRepositoryPort {

  private final PokeHistoryJpaRepository pokeHistoryJpaRepository;
  private final PokeHistoryQuerydslRepository pokeHistoryQuerydslRepository;

  @Override
  public Optional<PokeHistory> findById(Long pokeHistoryId) {
    return pokeHistoryJpaRepository.findById(pokeHistoryId).map(PokeHistoryEntity::toDomain);
  }

  @Override
  @Transactional
  public PokeHistory save(PokeHistory pokeHistory) {
    return pokeHistoryJpaRepository.save(PokeHistoryEntity.from(pokeHistory)).toDomain();
  }

  @Override
  @Transactional
  public void markReplied(Long pokeHistoryId) {
    pokeHistoryJpaRepository.markReplied(pokeHistoryId);
  }

  @Override
  public List<PokeHistory> findAllUnRepliedFromTo(Long pokerId, Long pokedId) {
    return toDomains(
        pokeHistoryJpaRepository.findAllByPokerIdAndPokedIdAndIsReplyFalse(pokerId, pokedId));
  }

  @Override
  public List<PokeHistory> findAllByPokedId(Long pokedId) {
    return toDomains(pokeHistoryJpaRepository.findAllByPokedId(pokedId));
  }

  @Override
  public List<PokeHistory> findAllByPokerIdAndIsReply(Long pokerId, boolean isReply) {
    return toDomains(pokeHistoryJpaRepository.findAllByPokerIdAndIsReply(pokerId, isReply));
  }

  @Override
  public List<PokeHistory> findAllFromToOrderByCreatedAtDesc(Long pokerId, Long pokedId) {
    return toDomains(
        pokeHistoryJpaRepository.findAllByPokerIdAndPokedIdOrderByCreatedAtDesc(pokerId, pokedId));
  }

  @Override
  public Page<PokeHistory> findAllByIdInOrderByCreatedAtDesc(
      List<Long> historyIds, Pageable pageable) {
    return pokeHistoryJpaRepository
        .findAllByIdInOrderByCreatedAtDesc(historyIds, pageable)
        .map(PokeHistoryEntity::toDomain);
  }

  @Override
  public List<PokeHistory> findAllUnRepliedBetweenOrderByCreatedAtDesc(Long userId, Long friendId) {
    return toDomains(
        pokeHistoryJpaRepository.findAllUnRepliedBetweenOrderByCreatedAtDesc(userId, friendId));
  }

  @Override
  public List<PokeHistory> findAllBetweenOrderByCreatedAtDesc(Long userId, Long friendId) {
    return toDomains(pokeHistoryJpaRepository.findAllBetweenOrderByCreatedAtDesc(userId, friendId));
  }

  @Override
  public long countByPokedIdAndIsReplyFalse(Long pokedId) {
    return pokeHistoryJpaRepository.countByPokedIdAndIsReplyFalse(pokedId);
  }

  @Override
  public long countByPokerId(Long pokerId) {
    return pokeHistoryJpaRepository.countByPokerId(pokerId);
  }

  @Override
  public Optional<PokeHistory> findRandomUnRepliedPokeMe(Long userId) {
    return pokeHistoryQuerydslRepository
        .findRandomUnRepliedPokeMe(userId)
        .map(PokeHistoryEntity::toDomain);
  }

  private List<PokeHistory> toDomains(List<PokeHistoryEntity> entities) {
    return entities.stream().map(PokeHistoryEntity::toDomain).toList();
  }
}
