package org.sopt.makers.domain.official.recruit.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.official.recruit.RecruitPartIntroduction;

public interface RecruitPartIntroductionRepositoryPort {

  Optional<RecruitPartIntroduction> findByGenerationAndPart(Integer generationId, Part part);

  List<RecruitPartIntroduction> findByGenerationId(Integer generationId);

  List<RecruitPartIntroduction> saveAll(List<RecruitPartIntroduction> introductions);

  void deleteByGenerationId(Integer generationId);
}
