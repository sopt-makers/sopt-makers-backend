package org.sopt.makers.storage.db.official.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.generation.Generation;
import org.sopt.makers.domain.official.generation.port.GenerationRepositoryPort;
import org.sopt.makers.storage.db.official.entity.GenerationEntity;
import org.sopt.makers.storage.db.official.repository.GenerationJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenerationRepositoryAdapter implements GenerationRepositoryPort {

  private final GenerationJpaRepository generationJpaRepository;

  @Override
  public Optional<Generation> findLatest() {
    return generationJpaRepository.findFirstByOrderByIdDesc().map(GenerationEntity::toDomain);
  }
}
