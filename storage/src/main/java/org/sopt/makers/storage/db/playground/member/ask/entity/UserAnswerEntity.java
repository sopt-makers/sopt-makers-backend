package org.sopt.makers.storage.db.playground.member.ask.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.playground.member.ask.UserAnswer;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "member_answer")
public class UserAnswerEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "answer_id")
  private Long id;

  @Column(name = "question_id", nullable = false, unique = true)
  private Long questionId;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @Builder(access = PRIVATE)
  private UserAnswerEntity(Long id, Long questionId, String content) {
    this.id = id;
    this.questionId = questionId;
    this.content = content;
  }

  public UserAnswer toDomain() {
    return new UserAnswer(id, questionId, content, getCreatedAt(), getUpdatedAt());
  }

  public static UserAnswerEntity fromDomain(UserAnswer userAnswer) {
    return UserAnswerEntity.builder()
        .id(userAnswer.id())
        .questionId(userAnswer.questionId())
        .content(userAnswer.content())
        .build();
  }
}
