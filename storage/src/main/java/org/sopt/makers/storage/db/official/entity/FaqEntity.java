package org.sopt.makers.storage.db.official.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.official.faq.Faq;
import org.sopt.makers.domain.official.faq.FaqCategory;
import org.sopt.makers.storage.db.official.converter.FaqQuestionsConverter;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "faq")
public class FaqEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "category", nullable = false, length = 20)
  private FaqCategory category;

  @Convert(converter = FaqQuestionsConverter.class)
  @Column(name = "questions", nullable = false, columnDefinition = "text")
  private List<Faq.QuestionAnswer> questions;

  public Faq toDomain() {
    return new Faq(id, category, questions);
  }

  public static FaqEntity fromDomain(Faq faq) {
    FaqEntity entity = new FaqEntity();
    entity.id = faq.id();
    entity.category = faq.category();
    entity.questions = faq.questions();
    return entity;
  }
}
