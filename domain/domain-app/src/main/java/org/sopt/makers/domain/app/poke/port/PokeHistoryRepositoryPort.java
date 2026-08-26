package org.sopt.makers.domain.app.poke.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.poke.PokeHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PokeHistoryRepositoryPort {

  Optional<PokeHistory> findById(Long pokeHistoryId);

  PokeHistory save(PokeHistory pokeHistory);

  void markReplied(Long pokeHistoryId);

  List<PokeHistory> findAllUnRepliedFromTo(Long pokerId, Long pokedId);

  List<PokeHistory> findAllByPokedId(Long pokedId);

  List<PokeHistory> findAllByPokerIdAndIsReply(Long pokerId, boolean isReply);

  List<PokeHistory> findAllFromToOrderByCreatedAtDesc(Long pokerId, Long pokedId);

  Page<PokeHistory> findAllByIdInOrderByCreatedAtDesc(List<Long> historyIds, Pageable pageable);

  List<PokeHistory> findAllUnRepliedBetweenOrderByCreatedAtDesc(Long userId, Long friendId);

  List<PokeHistory> findAllBetweenOrderByCreatedAtDesc(Long userId, Long friendId);

  long countByPokedIdAndIsReplyFalse(Long pokedId);

  long countByPokerId(Long pokerId);

  Optional<PokeHistory> findRandomUnRepliedPokeMe(Long userId);
}
