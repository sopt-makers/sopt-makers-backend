package org.sopt.makers.storage.db.official.adapter;

import java.util.List;
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

  @Override
  public List<RecruitPartIntroduction> findByGenerationId(Integer generationId) {
    return recruitPartIntroductionJpaRepository.findByGenerationId(generationId).stream()
        .map(RecruitPartIntroductionEntity::toDomain)
        .toList();
  }

  @Transactional
  @Override
  public List<RecruitPartIntroduction> saveAll(List<RecruitPartIntroduction> introductions) {
    List<RecruitPartIntroductionEntity> entities =
        introductions.stream().map(RecruitPartIntroductionEntity::fromDomain).toList();
    return recruitPartIntroductionJpaRepository.saveAll(entities).stream()
        .map(RecruitPartIntroductionEntity::toDomain)
        .toList();
  }

  @Transactional
  @Override
  public void deleteByGenerationId(Integer generationId) {
    recruitPartIntroductionJpaRepository.deleteByGenerationId(generationId);
  }
}
