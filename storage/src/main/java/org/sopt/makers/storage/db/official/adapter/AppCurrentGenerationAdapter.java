package org.sopt.makers.storage.db.official.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.generation.port.CurrentGenerationPort;
import org.sopt.makers.storage.db.official.entity.GenerationEntity;
import org.sopt.makers.storage.db.official.repository.GenerationJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppCurrentGenerationAdapter implements CurrentGenerationPort {

  private final GenerationJpaRepository generationJpaRepository;

  @Override
  public int getCurrentGeneration() {
    return generationJpaRepository
        .findFirstByOrderByIdDesc()
        .map(GenerationEntity::getId)
        .orElseThrow(() -> new IllegalStateException("기수 정보가 비어 있습니다. generation 테이블을 확인해야 합니다."));
  }
}
