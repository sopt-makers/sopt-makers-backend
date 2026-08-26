package org.sopt.makers.domain.app.operationconfig;

public record OperationConfig(
    Long id,
    String key,
    String value,
    OperationConfigType type,
    OperationConfigCategory category,
    String description) {

  public static OperationConfig text(
      OperationConfigCategory category, String key, String value, String description) {
    return new OperationConfig(null, key, value, OperationConfigType.TEXT, category, description);
  }

  public OperationConfig withValue(String newValue, String newDescription) {
    return new OperationConfig(id, key, newValue, type, category, newDescription);
  }
}
