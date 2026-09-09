package org.sopt.makers.storage.db.playground.community.vote.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.playground.community.vote.Vote;
import org.sopt.makers.domain.playground.community.vote.VoteOption;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "vote")
public class VoteEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "post_id", nullable = false, unique = true)
  private Long postId;

  @Column(name = "is_multiple_options", nullable = false)
  private boolean isMultipleOptions;

  @Builder(access = PROTECTED)
  private VoteEntity(Long postId, boolean isMultipleOptions) {
    this.postId = postId;
    this.isMultipleOptions = isMultipleOptions;
  }

  public static VoteEntity from(Vote vote) {
    return VoteEntity.builder()
        .postId(vote.postId())
        .isMultipleOptions(vote.isMultipleOptions())
        .build();
  }

  public Vote toDomain(List<VoteOption> options) {
    return new Vote(id, postId, isMultipleOptions, options, getCreatedAt(), getUpdatedAt());
  }
}
