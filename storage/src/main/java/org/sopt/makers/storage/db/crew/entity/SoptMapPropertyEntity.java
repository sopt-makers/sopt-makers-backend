package org.sopt.makers.storage.db.crew.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.storage.db.common.BaseEntity;
import org.sopt.makers.storage.db.crew.converter.ObjectMapConverter;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "property")
public class SoptMapPropertyEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "property_key", nullable = false, unique = true)
  private String key;

  @Convert(converter = ObjectMapConverter.class)
  @Column(nullable = false, columnDefinition = "TEXT")
  private Map<String, Object> properties;
}
