package org.sopt.makers.storage.db.app.poke.repository;

import java.util.List;
import org.sopt.makers.domain.app.poke.PokeMessageType;
import org.sopt.makers.storage.db.app.poke.entity.PokeMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PokeMessageJpaRepository extends JpaRepository<PokeMessageEntity, Long> {

  List<PokeMessageEntity> findAllByType(PokeMessageType type);
}
