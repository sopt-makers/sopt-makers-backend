package org.sopt.makers.storage.db.app.poke.repository;

import java.util.List;
import org.sopt.makers.storage.db.app.poke.entity.PokeHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PokeHistoryJpaRepository extends JpaRepository<PokeHistoryEntity, Long> {

  List<PokeHistoryEntity> findAllByPokerIdAndPokedIdAndIsReplyFalse(Long pokerId, Long pokedId);

  List<PokeHistoryEntity> findAllByPokedId(Long pokedId);

  List<PokeHistoryEntity> findAllByPokerIdAndIsReply(Long pokerId, boolean isReply);

  List<PokeHistoryEntity> findAllByPokerIdAndPokedIdOrderByCreatedAtDesc(
      Long pokerId, Long pokedId);

  Page<PokeHistoryEntity> findAllByIdInOrderByCreatedAtDesc(List<Long> ids, Pageable pageable);

  long countByPokedIdAndIsReplyFalse(Long pokedId);

  long countByPokerId(Long pokerId);

  @Query(
      """
      SELECT ph FROM PokeHistoryEntity ph
      WHERE ((ph.pokerId = :userId AND ph.pokedId = :friendId)
          OR (ph.pokerId = :friendId AND ph.pokedId = :userId))
        AND ph.isReply = false
      ORDER BY ph.createdAt DESC
      """)
  List<PokeHistoryEntity> findAllUnRepliedBetweenOrderByCreatedAtDesc(
      @Param("userId") Long userId, @Param("friendId") Long friendId);

  @Query(
      """
      SELECT ph FROM PokeHistoryEntity ph
      WHERE (ph.pokerId = :userId AND ph.pokedId = :friendId)
         OR (ph.pokerId = :friendId AND ph.pokedId = :userId)
      ORDER BY ph.createdAt DESC
      """)
  List<PokeHistoryEntity> findAllBetweenOrderByCreatedAtDesc(
      @Param("userId") Long userId, @Param("friendId") Long friendId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      "UPDATE PokeHistoryEntity ph SET ph.isReply = true, ph.updatedAt = CURRENT_TIMESTAMP"
          + " WHERE ph.id = :pokeHistoryId")
  void markReplied(@Param("pokeHistoryId") Long pokeHistoryId);
}
