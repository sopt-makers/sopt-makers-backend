package org.sopt.makers.storage.db.app.poke.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.app.poke.PokeHistory;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Table(name = "poke_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PokeHistoryEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long pokerId;

  private Long pokedId;

  @Column(columnDefinition = "TEXT")
  private String message;

  private boolean isReply;

  private boolean isAnonymous;

  public static PokeHistoryEntity from(PokeHistory pokeHistory) {
    return PokeHistoryEntity.builder()
        .id(pokeHistory.id())
        .pokerId(pokeHistory.pokerId())
        .pokedId(pokeHistory.pokedId())
        .message(pokeHistory.message())
        .isReply(pokeHistory.isReply())
        .isAnonymous(pokeHistory.isAnonymous())
        .build();
  }

  public PokeHistory toDomain() {
    return new PokeHistory(
        id, pokerId, pokedId, message, isReply, isAnonymous, getCreatedAt(), getUpdatedAt());
  }
}
