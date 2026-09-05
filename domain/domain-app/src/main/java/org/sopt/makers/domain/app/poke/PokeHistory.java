package org.sopt.makers.domain.app.poke;

import java.time.LocalDateTime;

public record PokeHistory(
    Long id,
    Long pokerId,
    Long pokedId,
    String message,
    boolean isReply,
    boolean isAnonymous,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public static PokeHistory createNew(
      Long pokerId, Long pokedId, String message, boolean isAnonymous) {
    return new PokeHistory(null, pokerId, pokedId, message, false, isAnonymous, null, null);
  }
}
