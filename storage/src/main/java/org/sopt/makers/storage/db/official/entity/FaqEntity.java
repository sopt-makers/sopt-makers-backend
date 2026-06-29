package org.sopt.makers.storage.db.official.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.official.faq.Faq;

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
  @Column(name = "part", nullable = false, length = 20)
  private Part part;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "questions", nullable = false, columnDefinition = "text")
  private List<Faq.QuestionAnswer> questions;

  public Faq toDomain() {
    return new Faq(id, part, questions == null ? List.of() : questions);
  }
}
