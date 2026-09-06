package org.sopt.makers.storage.db.app.home.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.home.AppService;
import org.sopt.makers.domain.app.home.port.AppServiceRepositoryPort;
import org.sopt.makers.storage.db.app.home.entity.AppServiceEntity;
import org.sopt.makers.storage.db.app.home.repository.AppServiceJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppServiceRepositoryAdapter implements AppServiceRepositoryPort {

  private final AppServiceJpaRepository appServiceJpaRepository;

  @Override
  public List<AppService> findAll() {
    return appServiceJpaRepository.findAll().stream().map(AppServiceEntity::toDomain).toList();
  }

  @Override
  public Optional<AppService> findByServiceName(String serviceName) {
    return appServiceJpaRepository.findByServiceName(serviceName).map(AppServiceEntity::toDomain);
  }
}
