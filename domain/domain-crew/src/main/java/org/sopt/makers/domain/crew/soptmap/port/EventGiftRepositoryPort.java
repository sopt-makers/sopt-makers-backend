package org.sopt.makers.domain.crew.soptmap.port;

import java.util.Optional;
import org.sopt.makers.domain.crew.soptmap.EventGift;

public interface EventGiftRepositoryPort {

  EventGift save(EventGift eventGift);

  boolean existsByUserId(Long userId);

  Optional<EventGift> findFirstClaimableForUpdate();

  Optional<EventGift> findActiveByUserIdAndMapId(Long userId, Long mapId);
}
