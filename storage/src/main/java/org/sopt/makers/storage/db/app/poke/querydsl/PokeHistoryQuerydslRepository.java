package org.sopt.makers.storage.db.app.poke.querydsl;

import java.util.Optional;
import org.sopt.makers.storage.db.app.poke.entity.PokeHistoryEntity;

public interface PokeHistoryQuerydslRepository {

  Optional<PokeHistoryEntity> findRandomUnRepliedPokeMe(Long userId);
}
