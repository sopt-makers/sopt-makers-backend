package org.sopt.makers.storage.db.app.poke.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.poke.PokeMessage;
import org.sopt.makers.domain.app.poke.PokeMessageType;
import org.sopt.makers.domain.app.poke.port.PokeMessageRepositoryPort;
import org.sopt.makers.storage.db.app.poke.entity.PokeMessageEntity;
import org.sopt.makers.storage.db.app.poke.repository.PokeMessageJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PokeMessageRepositoryAdapter implements PokeMessageRepositoryPort {

  private final PokeMessageJpaRepository pokeMessageJpaRepository;

  @Override
  public List<PokeMessage> findAllByType(PokeMessageType type) {
    return pokeMessageJpaRepository.findAllByType(type).stream()
        .map(PokeMessageEntity::toDomain)
        .toList();
  }
}
