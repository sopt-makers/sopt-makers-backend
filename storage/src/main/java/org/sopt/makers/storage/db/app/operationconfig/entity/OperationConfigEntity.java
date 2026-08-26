package org.sopt.makers.storage.db.app.operationconfig.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.app.operationconfig.OperationConfig;
import org.sopt.makers.domain.app.operationconfig.OperationConfigCategory;
import org.sopt.makers.domain.app.operationconfig.OperationConfigType;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "operation_configs",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uq_operation_configs_category_key",
            columnNames = {"category", "config_key"}))
public class OperationConfigEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "config_key", nullable = false, length = 255)
  private String key;

  @Column(name = "config_value", nullable = false, length = 500)
  private String value;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 20)
  private OperationConfigType type;

  @Enumerated(EnumType.STRING)
  @Column(name = "category", nullable = false, length = 30)
  private OperationConfigCategory category;

  @Column(name = "description", nullable = false, length = 255)
  private String description;

  private OperationConfigEntity(
      String key,
      String value,
      OperationConfigType type,
      OperationConfigCategory category,
      String description) {
    this.key = key;
    this.value = value;
    this.type = type;
    this.category = category;
    this.description = description;
  }

  public static OperationConfigEntity from(OperationConfig operationConfig) {
    return new OperationConfigEntity(
        operationConfig.key(),
        operationConfig.value(),
        operationConfig.type(),
        operationConfig.category(),
        operationConfig.description());
  }

  public OperationConfig toDomain() {
    return new OperationConfig(id, key, value, type, category, description);
  }

  public void updateValue(String newValue) {
    this.value = newValue;
  }
}
