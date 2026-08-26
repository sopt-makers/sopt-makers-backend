package org.sopt.makers.storage.db.crew.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.crew.soptmap.EventGift;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "event_gift")
public class EventGiftEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "map_id")
  private Long mapId;

  @Column(name = "gift_url", nullable = false)
  private String giftUrl;

  @Column(nullable = false)
  private Boolean claimable;

  @Column(nullable = false)
  private Boolean active;

  @Builder(access = PRIVATE)
  private EventGiftEntity(
      Long id, Long userId, Long mapId, String giftUrl, Boolean claimable, Boolean active) {
    this.id = id;
    this.userId = userId;
    this.mapId = mapId;
    this.giftUrl = giftUrl;
    this.claimable = claimable;
    this.active = active;
  }

  public EventGift toDomain() {
    return new EventGift(
        id, userId, mapId, giftUrl, claimable, active, getCreatedAt(), getUpdatedAt());
  }

  public static EventGiftEntity fromDomain(EventGift gift) {
    return EventGiftEntity.builder()
        .id(gift.id())
        .userId(gift.userId())
        .mapId(gift.mapId())
        .giftUrl(gift.giftUrl())
        .claimable(gift.claimable())
        .active(gift.active())
        .build();
  }
}
