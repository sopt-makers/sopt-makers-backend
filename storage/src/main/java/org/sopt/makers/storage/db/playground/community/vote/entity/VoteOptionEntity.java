package org.sopt.makers.storage.db.playground.community.vote.entity;

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
import org.sopt.makers.domain.playground.community.vote.VoteOption;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "vote_option")
public class VoteOptionEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "vote_id", nullable = false)
  private Long voteId;

  @Column(name = "content", nullable = false)
  private String content;

  @Column(name = "vote_count", nullable = false)
  private int voteCount;

  @Builder(access = PROTECTED)
  private VoteOptionEntity(Long voteId, String content, int voteCount) {
    this.voteId = voteId;
    this.content = content;
    this.voteCount = voteCount;
  }

  public static VoteOptionEntity of(Long voteId, VoteOption option) {
    return VoteOptionEntity.builder().voteId(voteId).content(option.content()).voteCount(option.voteCount()).build();
  }

  public VoteOption toDomain() {
    return new VoteOption(id, content, voteCount);
  }
}
