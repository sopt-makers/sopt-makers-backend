package org.sopt.makers.domain.official.generation.service;

import lombok.RequiredArgsConstructor;
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
}
