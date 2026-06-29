package org.sopt.makers.storage.db.official.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.official.part.port.PartRepositoryPort;
import org.sopt.makers.domain.official.recruit.RecruitPartInfo;
import org.sopt.makers.storage.db.official.entity.RecruitPartEntity;
import org.sopt.makers.storage.db.official.repository.RecruitPartJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartRepositoryAdapter implements PartRepositoryPort {

  private final RecruitPartJpaRepository recruitPartJpaRepository;

  @Override
  public List<RecruitPartInfo> findByGeneration(Integer generationId) {
    return recruitPartJpaRepository.findByGenerationIdOrderByPartTypeAsc(generationId).stream()
        .map(RecruitPartEntity::toDomain)
        .toList();
  }

  @Override
  public Optional<RecruitPartInfo> findByGenerationAndPart(Integer generationId, Part part) {
    return recruitPartJpaRepository
        .findByGenerationIdAndPartType(generationId, part)
        .map(RecruitPartEntity::toDomain);
  }
}
