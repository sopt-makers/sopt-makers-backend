package org.sopt.makers.storage.db.admin.repository;

import java.util.List;
import org.sopt.makers.domain.admin.banner.PublishLocation;
import org.sopt.makers.storage.db.admin.entity.BannerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerJpaRepository extends JpaRepository<BannerEntity, Long> {

  List<BannerEntity> findBannersByLocation(PublishLocation publishLocation);
}
