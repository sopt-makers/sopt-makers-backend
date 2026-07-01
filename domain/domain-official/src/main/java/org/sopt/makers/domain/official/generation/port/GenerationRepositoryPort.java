package org.sopt.makers.domain.official.generation.port;

import java.util.Optional;
import org.sopt.makers.domain.official.generation.BrandingColor;
import org.sopt.makers.domain.official.generation.Generation;

public interface GenerationRepositoryPort {

  Optional<Generation> findLatest();

  Optional<Generation> findById(Integer id);

  Generation createOrUpdate(Integer id, String name, BrandingColor brandingColor);

  void updateHeaderImage(Integer id, String headerImage);

  void updateHomeHeaderImage(Integer id, String homeHeaderImage);

  void updateRecruitHeaderImage(Integer id, String recruitHeaderImage);
}
