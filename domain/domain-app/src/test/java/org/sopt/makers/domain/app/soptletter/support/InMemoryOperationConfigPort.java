package org.sopt.makers.domain.app.soptletter.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.operationconfig.OperationConfig;
import org.sopt.makers.domain.app.operationconfig.OperationConfigCategory;
import org.sopt.makers.domain.app.operationconfig.port.OperationConfigPort;

public final class InMemoryOperationConfigPort implements OperationConfigPort {

  private final List<OperationConfig> store = new ArrayList<>();
  private long sequence = 1L;

  @Override
  public List<OperationConfig> findAllByCategory(OperationConfigCategory category) {
    return store.stream().filter(config -> config.category() == category).toList();
  }

  @Override
  public Optional<OperationConfig> findByCategoryAndKey(
      OperationConfigCategory category, String key) {
    return findAllByCategory(category).stream()
        .filter(config -> config.key().equals(key))
        .findFirst();
  }

  @Override
  public OperationConfig save(OperationConfig operationConfig) {
    OperationConfig saved =
        new OperationConfig(
            operationConfig.id() == null ? sequence++ : operationConfig.id(),
            operationConfig.key(),
            operationConfig.value(),
            operationConfig.type(),
            operationConfig.category(),
            operationConfig.description());
    store.removeIf(config -> config.id() != null && config.id().equals(saved.id()));
    store.add(saved);
    return saved;
  }
}
