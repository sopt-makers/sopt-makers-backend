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
import org.sopt.makers.domain.playground.member.ask.AnswerReaction;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "answer_reaction",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_answer_reaction_answer_member",
          columnNames = {"answer_id", "member_id"})
    })
public class AnswerReactionEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "reaction_id")
  private Long id;

  @Column(name = "answer_id", nullable = false)
  private Long answerId;

  @Column(name = "member_id", nullable = false)
  private Long reactorUserId;

  @Builder(access = PRIVATE)
  private AnswerReactionEntity(Long id, Long answerId, Long reactorUserId) {
    this.id = id;
    this.answerId = answerId;
    this.reactorUserId = reactorUserId;
  }

  public AnswerReaction toDomain() {
    return new AnswerReaction(id, answerId, reactorUserId, getCreatedAt(), getUpdatedAt());
  }

  public static AnswerReactionEntity fromDomain(AnswerReaction answerReaction) {
    return AnswerReactionEntity.builder()
        .id(answerReaction.id())
        .answerId(answerReaction.answerId())
        .reactorUserId(answerReaction.reactorUserId())
        .build();
  }
}
