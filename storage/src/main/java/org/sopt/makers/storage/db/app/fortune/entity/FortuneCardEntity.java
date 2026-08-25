package org.sopt.makers.storage.db.app.fortune.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.app.fortune.FortuneCard;

@Entity
@Table(name = "fortune_card")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FortuneCardEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private String description;

  @Column(columnDefinition = "TEXT")
  private String imageUrl;

  private String imageColorCode;

  public FortuneCard toDomain() {
    return new FortuneCard(id, name, description, imageUrl, imageColorCode);
  }
}
