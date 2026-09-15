package org.sopt.makers.storage.db.app.home.repository;

import java.util.Optional;
import org.sopt.makers.storage.db.app.home.entity.AppServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppServiceJpaRepository extends JpaRepository<AppServiceEntity, Long> {

  Optional<AppServiceEntity> findByServiceName(String serviceName);
}
