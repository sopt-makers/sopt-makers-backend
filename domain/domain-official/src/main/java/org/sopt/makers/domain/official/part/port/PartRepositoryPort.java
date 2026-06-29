package org.sopt.makers.domain.official.part.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.official.recruit.RecruitPartInfo;

public interface PartRepositoryPort {

  List<RecruitPartInfo> findByGeneration(Integer generationId);

  Optional<RecruitPartInfo> findByGenerationAndPart(Integer generationId, Part part);
}
