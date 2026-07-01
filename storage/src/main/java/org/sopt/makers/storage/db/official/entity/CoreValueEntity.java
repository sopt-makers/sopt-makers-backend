package org.sopt.makers.storage.db.official.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.official.corevalue.CoreValue;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "core_value")
public class CoreValueEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "generation_id", nullable = false)
  private Integer generationId;

  @Column(name = "value", nullable = false, length = 50)
  private String value;

  @Column(name = "description", nullable = false, length = 500)
  private String description;

  @Column(name = "detail_description", nullable = false, length = 100)
  private String detailDescription;

  @Column(name = "image_url", nullable = false, length = 500)
  private String imageUrl;

  @Column(name = "display_order", nullable = false)
  private int displayOrder;

  public CoreValue toDomain() {
    return new CoreValue(
        id, generationId, value, description, detailDescription, imageUrl, displayOrder);
  }

  public static CoreValueEntity fromDomain(CoreValue coreValue) {
    CoreValueEntity entity = new CoreValueEntity();
    entity.id = coreValue.id();
    entity.generationId = coreValue.generationId();
    entity.value = coreValue.value();
    entity.description = coreValue.description();
    entity.detailDescription = coreValue.detailDescription();
    entity.imageUrl = coreValue.imageUrl() != null ? coreValue.imageUrl() : "";
    entity.displayOrder = coreValue.displayOrder();
    return entity;
  }
}
