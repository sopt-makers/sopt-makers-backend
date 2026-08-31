package org.sopt.makers.domain.crew.soptmap;

import java.time.LocalDateTime;

public record EventGift(
    Long id,
    Long userId,
    Long mapId,
    String giftUrl,
    boolean claimable,
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public EventGift claim(Long receiverUserId, Long soptMapId) {
    return new EventGift(
        id, receiverUserId, soptMapId, giftUrl, false, active, createdAt, updatedAt);
  }

  public EventGift use() {
    return new EventGift(id, userId, mapId, giftUrl, claimable, false, createdAt, updatedAt);
  }
}
