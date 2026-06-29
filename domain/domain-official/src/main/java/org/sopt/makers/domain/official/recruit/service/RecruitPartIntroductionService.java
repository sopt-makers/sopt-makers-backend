package org.sopt.makers.domain.official.recruit.service;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.official.recruit.RecruitPartIntroduction;
import org.sopt.makers.domain.official.recruit.exception.RecruitException;
import org.sopt.makers.domain.official.recruit.exception.RecruitFailure;
import org.sopt.makers.domain.official.recruit.port.RecruitPartIntroductionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitPartIntroductionService {

  private final RecruitPartIntroductionRepositoryPort recruitPartIntroductionRepositoryPort;

  public RecruitPartIntroduction findByGenerationAndPart(Integer generationId, Part part) {
    return recruitPartIntroductionRepositoryPort
        .findByGenerationAndPart(generationId, part)
        .orElseThrow(
            () -> new RecruitException(RecruitFailure.RECRUIT_PART_INTRODUCTION_NOT_FOUND));
  }
}
