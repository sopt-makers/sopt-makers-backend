package org.sopt.makers.storage.db.app.operationconfig.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.operationconfig.OperationConfigCategory;
import org.sopt.makers.storage.db.app.operationconfig.entity.OperationConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationConfigJpaRepository extends JpaRepository<OperationConfigEntity, Long> {

  List<OperationConfigEntity> findAllByCategory(OperationConfigCategory category);

  Optional<OperationConfigEntity> findByCategoryAndKey(
      OperationConfigCategory category, String key);
}
