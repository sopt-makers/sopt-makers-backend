package org.sopt.makers.domain.official.soptstory.port;

import java.util.Optional;
import org.sopt.makers.domain.official.soptstory.SoptStory;
import org.sopt.makers.domain.official.soptstory.SoptStorySortType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SoptStoryRepositoryPort {

  boolean existsByUrl(String url);

  Optional<SoptStory> findById(Long id);

  SoptStory save(SoptStory soptStory);

  Page<SoptStory> findAll(SoptStorySortType sortType, Pageable pageable);
}
