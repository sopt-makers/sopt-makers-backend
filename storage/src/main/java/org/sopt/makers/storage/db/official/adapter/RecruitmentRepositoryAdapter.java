package org.sopt.makers.storage.db.official.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.recruitment.Recruitment;
import org.sopt.makers.domain.official.recruitment.port.RecruitmentRepositoryPort;
import org.sopt.makers.storage.db.official.entity.RecruitmentEntity;
import org.sopt.makers.storage.db.official.repository.RecruitmentJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentRepositoryAdapter implements RecruitmentRepositoryPort {

  private final RecruitmentJpaRepository recruitmentJpaRepository;

  @Transactional
  @Override
  public List<Recruitment> saveAll(List<Recruitment> recruitments) {
    List<RecruitmentEntity> entities =
        recruitments.stream().map(RecruitmentEntity::fromDomain).toList();
    return recruitmentJpaRepository.saveAll(entities).stream()
        .map(RecruitmentEntity::toDomain)
        .toList();
  }

  @Transactional
  @Override
  public void deleteByGenerationId(Integer generationId) {
    recruitmentJpaRepository.deleteByGenerationId(generationId);
  }

  @Override
  public List<Recruitment> findByGenerationId(Integer generationId) {
    return recruitmentJpaRepository.findByGenerationIdOrderByRecruitTypeAsc(generationId).stream()
        .map(RecruitmentEntity::toDomain)
        .toList();
  }
}
