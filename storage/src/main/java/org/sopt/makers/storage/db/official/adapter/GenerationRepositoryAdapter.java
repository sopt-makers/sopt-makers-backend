package org.sopt.makers.storage.db.official.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.generation.BrandingColor;
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

  @Override
  public Optional<Generation> findById(Integer id) {
    return generationJpaRepository.findById(id).map(GenerationEntity::toDomain);
  }

  @Transactional
  @Override
  public Generation createOrUpdate(Integer id, String name, BrandingColor brandingColor) {
    GenerationEntity entity =
        generationJpaRepository
            .findById(id)
            .orElseGet(() -> GenerationEntity.create(id, name, brandingColor));
    entity.update(name, brandingColor);
    return generationJpaRepository.save(entity).toDomain();
  }

  @Transactional
  @Override
  public void updateHeaderImage(Integer id, String headerImage) {
    generationJpaRepository
        .findById(id)
        .ifPresent(
            entity -> {
              entity.updateHeaderImage(headerImage);
              generationJpaRepository.save(entity);
            });
  }

  @Transactional
  @Override
  public void updateHomeHeaderImage(Integer id, String homeHeaderImage) {
    generationJpaRepository
        .findById(id)
        .ifPresent(
            entity -> {
              entity.updateHomeHeaderImage(homeHeaderImage);
              generationJpaRepository.save(entity);
            });
  }

  @Transactional
  @Override
  public void updateRecruitHeaderImage(Integer id, String recruitHeaderImage) {
    generationJpaRepository
        .findById(id)
        .ifPresent(
            entity -> {
              entity.updateRecruitHeaderImage(recruitHeaderImage);
              generationJpaRepository.save(entity);
            });
  }
}
