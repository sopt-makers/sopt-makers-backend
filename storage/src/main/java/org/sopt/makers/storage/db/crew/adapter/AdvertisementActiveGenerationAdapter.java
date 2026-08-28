package org.sopt.makers.storage.db.crew.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.advertisement.port.AdvertisementActiveGenerationPort;
import org.sopt.makers.storage.db.official.entity.GenerationEntity;
import org.sopt.makers.storage.db.official.repository.GenerationJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdvertisementActiveGenerationAdapter implements AdvertisementActiveGenerationPort {

  private final GenerationJpaRepository generationJpaRepository;

  @Override
  public int getActiveGeneration() {
    return generationJpaRepository
        .findFirstByOrderByIdDesc()
        .map(GenerationEntity::getId)
        .orElseThrow(() -> new IllegalStateException("기수 정보가 비어 있습니다."));
  }
}
