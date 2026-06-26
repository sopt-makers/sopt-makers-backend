package org.sopt.makers.storage.db.official.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.soptstory.SoptStory;
import org.sopt.makers.domain.official.soptstory.SoptStoryLike;
import org.sopt.makers.domain.official.soptstory.port.SoptStoryLikeRepositoryPort;
import org.sopt.makers.storage.db.official.entity.SoptStoryEntity;
import org.sopt.makers.storage.db.official.entity.SoptStoryLikeEntity;
import org.sopt.makers.storage.db.official.repository.SoptStoryLikeJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoptStoryLikeRepositoryAdapter implements SoptStoryLikeRepositoryPort {

  private final SoptStoryLikeJpaRepository soptStoryLikeJpaRepository;

  @Override
  public boolean existsBySoptStoryIdAndIp(Long soptStoryId, String ip) {
    return soptStoryLikeJpaRepository.existsBySoptStory_IdAndIp(soptStoryId, ip);
  }

  @Override
  public Optional<SoptStoryLike> findBySoptStoryIdAndIp(Long soptStoryId, String ip) {
    return soptStoryLikeJpaRepository
        .findBySoptStory_IdAndIp(soptStoryId, ip)
        .map(SoptStoryLikeEntity::toDomain);
  }

  @Override
  public List<SoptStoryLike> findAllByIpAndSoptStoryIn(String ip, List<SoptStory> soptStories) {
    List<SoptStoryEntity> entities = soptStories.stream().map(SoptStoryEntity::fromDomain).toList();
    return soptStoryLikeJpaRepository.findAllByIpAndSoptStoryIn(ip, entities).stream()
        .map(SoptStoryLikeEntity::toDomain)
        .toList();
  }

  @Transactional
  @Override
  public SoptStoryLike save(SoptStoryLike like) {
    return soptStoryLikeJpaRepository.save(SoptStoryLikeEntity.fromDomain(like)).toDomain();
  }

  @Transactional
  @Override
  public void delete(SoptStoryLike like) {
    soptStoryLikeJpaRepository.delete(SoptStoryLikeEntity.fromDomain(like));
  }
}
