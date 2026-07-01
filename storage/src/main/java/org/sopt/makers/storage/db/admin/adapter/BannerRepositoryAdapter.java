package org.sopt.makers.storage.db.admin.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.admin.banner.Banner;
import org.sopt.makers.domain.admin.banner.PublishLocation;
import org.sopt.makers.domain.admin.banner.port.BannerRepositoryPort;
import org.sopt.makers.storage.db.admin.entity.BannerEntity;
import org.sopt.makers.storage.db.admin.repository.BannerJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BannerRepositoryAdapter implements BannerRepositoryPort {

  private final BannerJpaRepository bannerJpaRepository;

  @Override
  public Optional<Banner> findById(Long id) {
    return bannerJpaRepository.findById(id).map(BannerEntity::toDomain);
  }

  @Override
  public List<Banner> findAll() {
    return bannerJpaRepository.findAll().stream().map(BannerEntity::toDomain).toList();
  }

  @Override
  public List<Banner> findByLocation(PublishLocation location) {
    return bannerJpaRepository.findBannersByLocation(location).stream()
        .map(BannerEntity::toDomain)
        .toList();
  }

  @Override
  @Transactional
  public Banner save(Banner banner) {
    return bannerJpaRepository.save(BannerEntity.from(banner)).toDomain();
  }

  @Override
  @Transactional
  public void delete(Banner banner) {
    bannerJpaRepository.delete(BannerEntity.from(banner));
  }
}
