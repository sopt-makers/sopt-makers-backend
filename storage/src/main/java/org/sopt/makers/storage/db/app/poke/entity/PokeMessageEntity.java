package org.sopt.makers.storage.db.app.poke.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.app.poke.PokeMessage;
import org.sopt.makers.domain.app.poke.PokeMessageType;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Table(name = "poke_message")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PokeMessageEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "TEXT")
  private String content;

  @Enumerated(EnumType.STRING)
  private PokeMessageType type;

  public static PokeMessageEntity from(PokeMessage pokeMessage) {
    return PokeMessageEntity.builder()
        .id(pokeMessage.id())
        .content(pokeMessage.content())
        .type(pokeMessage.type())
        .build();
  }

  public PokeMessage toDomain() {
    return new PokeMessage(id, content, type);
  }
}
