package org.sopt.makers.domain.admin.banner.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.admin.banner.Banner;
import org.sopt.makers.domain.admin.banner.PublishLocation;

public interface BannerRepositoryPort {

  Optional<Banner> findById(Long id);

  List<Banner> findAll();

  List<Banner> findByLocation(PublishLocation location);

  Banner save(Banner banner);

  void delete(Banner banner);
}
