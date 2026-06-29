package org.sopt.makers.storage.db.official.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.corevalue.CoreValue;
import org.sopt.makers.domain.official.corevalue.port.CoreValueRepositoryPort;
import org.sopt.makers.storage.db.official.entity.CoreValueEntity;
import org.sopt.makers.storage.db.official.repository.CoreValueJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoreValueRepositoryAdapter implements CoreValueRepositoryPort {

  private final CoreValueJpaRepository coreValueJpaRepository;

  @Override
  public List<CoreValue> findByGeneration(Integer generationId) {
    return coreValueJpaRepository.findByGenerationIdOrderByDisplayOrderAsc(generationId).stream()
        .map(CoreValueEntity::toDomain)
        .toList();
  }
}
