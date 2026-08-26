package org.sopt.makers.storage.db.app.operationconfig.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.operationconfig.OperationConfig;
import org.sopt.makers.domain.app.operationconfig.OperationConfigCategory;
import org.sopt.makers.domain.app.operationconfig.exception.OperationConfigException;
import org.sopt.makers.domain.app.operationconfig.exception.OperationConfigFailure;
import org.sopt.makers.domain.app.operationconfig.port.OperationConfigPort;
import org.sopt.makers.storage.db.app.operationconfig.entity.OperationConfigEntity;
import org.sopt.makers.storage.db.app.operationconfig.repository.OperationConfigJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OperationConfigRepositoryAdapter implements OperationConfigPort {

  private final OperationConfigJpaRepository operationConfigJpaRepository;

  @Override
  public List<OperationConfig> findAllByCategory(OperationConfigCategory category) {
    return operationConfigJpaRepository.findAllByCategory(category).stream()
        .map(OperationConfigEntity::toDomain)
        .toList();
  }

  @Override
  public Optional<OperationConfig> findByCategoryAndKey(
      OperationConfigCategory category, String key) {
    return operationConfigJpaRepository
        .findByCategoryAndKey(category, key)
        .map(OperationConfigEntity::toDomain);
  }

  @Override
  @Transactional
  public OperationConfig save(OperationConfig operationConfig) {
    if (operationConfig.id() == null) {
      return operationConfigJpaRepository
          .save(OperationConfigEntity.from(operationConfig))
          .toDomain();
    }
    OperationConfigEntity entity =
        operationConfigJpaRepository
            .findById(operationConfig.id())
            .orElseThrow(
                () ->
                    new OperationConfigException(
                        OperationConfigFailure.NOT_FOUND_OPERATION_CONFIG));
    entity.updateValue(operationConfig.value());
    return entity.toDomain();
  }
}
