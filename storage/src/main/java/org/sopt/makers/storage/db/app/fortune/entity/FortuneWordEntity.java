package org.sopt.makers.storage.db.app.fortune.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.app.fortune.FortuneWord;

@Entity
@Table(name = "fortune_word")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FortuneWordEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;

  private Long fortuneCardId;

  public FortuneWord toDomain() {
    return new FortuneWord(id, title, fortuneCardId);
  }
}
