package org.sopt.makers.storage.db.official.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.soptstory.SoptStory;
import org.sopt.makers.domain.official.soptstory.SoptStorySortType;
import org.sopt.makers.domain.official.soptstory.port.SoptStoryRepositoryPort;
import org.sopt.makers.storage.db.official.entity.SoptStoryEntity;
import org.sopt.makers.storage.db.official.repository.SoptStoryJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoptStoryRepositoryAdapter implements SoptStoryRepositoryPort {

  private final SoptStoryJpaRepository soptStoryJpaRepository;

  @Override
  public boolean existsByUrl(String url) {
    return soptStoryJpaRepository.existsByUrl(url);
  }

  @Override
  public Optional<SoptStory> findById(Long id) {
    return soptStoryJpaRepository.findById(id).map(SoptStoryEntity::toDomain);
  }

  @Transactional
  @Override
  public SoptStory save(SoptStory soptStory) {
    return soptStoryJpaRepository.save(SoptStoryEntity.fromDomain(soptStory)).toDomain();
  }

  @Override
  public Page<SoptStory> findAll(SoptStorySortType sortType, Pageable pageable) {
    Page<SoptStoryEntity> page =
        sortType == SoptStorySortType.LIKES
            ? soptStoryJpaRepository.findAllByOrderByLikeCountDesc(pageable)
            : soptStoryJpaRepository.findAllByOrderByCreatedAtDesc(pageable);
    return page.map(SoptStoryEntity::toDomain);
  }
}
