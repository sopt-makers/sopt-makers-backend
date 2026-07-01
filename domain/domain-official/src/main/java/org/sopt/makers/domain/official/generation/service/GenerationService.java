package org.sopt.makers.domain.official.generation.service;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.generation.BrandingColor;
import org.sopt.makers.domain.official.generation.Generation;
import org.sopt.makers.domain.official.generation.port.GenerationRepositoryPort;
import org.sopt.makers.domain.official.recruit.exception.RecruitException;
import org.sopt.makers.domain.official.recruit.exception.RecruitFailure;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenerationService {

  private final GenerationRepositoryPort generationRepositoryPort;

  public Generation findLatest() {
    return generationRepositoryPort
        .findLatest()
        .orElseThrow(() -> new RecruitException(RecruitFailure.GENERATION_NOT_FOUND));
  }

  public Generation findById(Integer id) {
    return generationRepositoryPort
        .findById(id)
        .orElseThrow(() -> new RecruitException(RecruitFailure.GENERATION_NOT_FOUND));
  }

  @Transactional
  public Generation createOrUpdate(Integer id, String name, BrandingColor brandingColor) {
    return generationRepositoryPort.createOrUpdate(id, name, brandingColor);
  }

  @Transactional
  public void updateHeaderImage(Integer id, String headerImage) {
    generationRepositoryPort.updateHeaderImage(id, headerImage);
  }

  @Transactional
  public void updateHomeHeaderImage(Integer id, String homeHeaderImage) {
    generationRepositoryPort.updateHomeHeaderImage(id, homeHeaderImage);
  }

  @Transactional
  public void updateRecruitHeaderImage(Integer id, String recruitHeaderImage) {
    generationRepositoryPort.updateRecruitHeaderImage(id, recruitHeaderImage);
  }
}
