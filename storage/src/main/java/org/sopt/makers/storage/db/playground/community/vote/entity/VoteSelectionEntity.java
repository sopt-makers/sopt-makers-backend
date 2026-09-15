package org.sopt.makers.storage.db.playground.community.vote.entity;

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
import org.sopt.makers.domain.playground.community.vote.VoteSelection;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "vote_selection",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_vote_selection_user_option",
          columnNames = {"user_id", "vote_option_id"})
    })
public class VoteSelectionEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "vote_option_id", nullable = false)
  private Long voteOptionId;

  @Builder(access = PROTECTED)
  private VoteSelectionEntity(Long userId, Long voteOptionId) {
    this.userId = userId;
    this.voteOptionId = voteOptionId;
  }

  public static VoteSelectionEntity from(VoteSelection voteSelection) {
    return VoteSelectionEntity.builder()
        .userId(voteSelection.userId())
        .voteOptionId(voteSelection.voteOptionId())
        .build();
  }

  public VoteSelection toDomain() {
    return new VoteSelection(id, userId, voteOptionId, getCreatedAt(), getUpdatedAt());
  }
}
