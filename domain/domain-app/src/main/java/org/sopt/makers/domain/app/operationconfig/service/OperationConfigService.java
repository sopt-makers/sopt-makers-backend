package org.sopt.makers.domain.app.operationconfig.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.operationconfig.OperationConfig;
import org.sopt.makers.domain.app.operationconfig.OperationConfigCategory;
import org.sopt.makers.domain.app.operationconfig.exception.OperationConfigException;
import org.sopt.makers.domain.app.operationconfig.exception.OperationConfigFailure;
import org.sopt.makers.domain.app.operationconfig.port.OperationConfigPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OperationConfigService {

  private final OperationConfigPort operationConfigPort;

  public List<OperationConfig> getAllByCategory(OperationConfigCategory category) {
    return operationConfigPort.findAllByCategory(category);
  }

  public Map<String, String> getValuesByCategory(OperationConfigCategory category) {
    return operationConfigPort.findAllByCategory(category).stream()
        .collect(Collectors.toMap(OperationConfig::key, OperationConfig::value));
  }

  public Optional<String> findValue(OperationConfigCategory category, String key) {
    return operationConfigPort.findByCategoryAndKey(category, key).map(OperationConfig::value);
  }

  public String getValue(OperationConfigCategory category, String key) {
    return findValue(category, key)
        .orElseThrow(
            () -> new OperationConfigException(OperationConfigFailure.NOT_FOUND_OPERATION_CONFIG));
  }

  @Transactional
  public void upsertValue(
      OperationConfigCategory category, String key, String value, String description) {
    OperationConfig config =
        operationConfigPort
            .findByCategoryAndKey(category, key)
            .map(existing -> existing.withValue(value, description))
            .orElseGet(() -> OperationConfig.text(category, key, value, description));
    operationConfigPort.save(config);
  }
}
