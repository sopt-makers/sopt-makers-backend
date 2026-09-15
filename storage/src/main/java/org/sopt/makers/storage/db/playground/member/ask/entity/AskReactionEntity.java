package org.sopt.makers.storage.db.playground.member.ask.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.playground.member.ask.AskReaction;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "question_reaction",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_question_reaction_question_member",
          columnNames = {"question_id", "member_id"})
    })
public class AskReactionEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "reaction_id")
  private Long id;

  @Column(name = "question_id", nullable = false)
  private Long questionId;

  @Column(name = "member_id", nullable = false)
  private Long reactorUserId;

  @Builder(access = PRIVATE)
  private AskReactionEntity(Long id, Long questionId, Long reactorUserId) {
    this.id = id;
    this.questionId = questionId;
    this.reactorUserId = reactorUserId;
  }

  public AskReaction toDomain() {
    return new AskReaction(id, questionId, reactorUserId, getCreatedAt(), getUpdatedAt());
  }

  public static AskReactionEntity fromDomain(AskReaction askReaction) {
    return AskReactionEntity.builder()
        .id(askReaction.id())
        .questionId(askReaction.questionId())
        .reactorUserId(askReaction.reactorUserId())
        .build();
  }
}
