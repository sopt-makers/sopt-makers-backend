package org.sopt.makers.storage.db.official.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.official.recruit.RecruitPartIntroduction;
import org.sopt.makers.domain.official.recruit.port.RecruitPartIntroductionRepositoryPort;
import org.sopt.makers.storage.db.official.entity.RecruitPartIntroductionEntity;
import org.sopt.makers.storage.db.official.repository.RecruitPartIntroductionJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitPartIntroductionRepositoryAdapter
    implements RecruitPartIntroductionRepositoryPort {

  private final RecruitPartIntroductionJpaRepository recruitPartIntroductionJpaRepository;

  @Override
  public Optional<RecruitPartIntroduction> findByGenerationAndPart(
      Integer generationId, Part part) {
    return recruitPartIntroductionJpaRepository
        .findByGenerationIdAndPart(generationId, part)
        .map(RecruitPartIntroductionEntity::toDomain);
  }
}
