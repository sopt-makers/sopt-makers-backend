package org.sopt.makers.domain.official.recruitment.port;

import java.util.List;
import org.sopt.makers.domain.official.recruitment.Recruitment;

public interface RecruitmentRepositoryPort {

  List<Recruitment> saveAll(List<Recruitment> recruitments);

  void deleteByGenerationId(Integer generationId);

  List<Recruitment> findByGenerationId(Integer generationId);
}
