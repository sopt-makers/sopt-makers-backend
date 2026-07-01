package org.sopt.makers.domain.official.part.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.official.part.port.PartRepositoryPort;
import org.sopt.makers.domain.official.recruit.RecruitPartInfo;
import org.sopt.makers.domain.official.recruit.exception.RecruitException;
import org.sopt.makers.domain.official.recruit.exception.RecruitFailure;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartService {

  private final PartRepositoryPort partRepositoryPort;

  public List<RecruitPartInfo> findByGeneration(Integer generationId) {
    return partRepositoryPort.findByGeneration(generationId);
  }

  public RecruitPartInfo findByGenerationAndPart(Integer generationId, Part part) {
    return partRepositoryPort
        .findByGenerationAndPart(generationId, part)
        .orElseThrow(() -> new RecruitException(RecruitFailure.PART_NOT_FOUND));
  }

  @Transactional
  public void bulkCreate(Integer generationId, List<RecruitPartInfo> parts) {
    partRepositoryPort.deleteByGenerationId(generationId);
    partRepositoryPort.saveAll(parts);
  }
}
