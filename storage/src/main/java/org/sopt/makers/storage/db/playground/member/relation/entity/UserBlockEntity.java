package org.sopt.makers.storage.db.playground.member.relation.entity;

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
import org.sopt.makers.domain.playground.member.relation.UserBlock;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "member_block")
public class UserBlockEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "blocker_id", nullable = false)
  private Long blockerUserId;

  @Column(name = "blocked_member_id", nullable = false)
  private Long blockedUserId;

  @Column(name = "is_blocked", nullable = false)
  private Boolean isBlocked;

  @Builder(access = PRIVATE)
  private UserBlockEntity(Long id, Long blockerUserId, Long blockedUserId, Boolean isBlocked) {
    this.id = id;
    this.blockerUserId = blockerUserId;
    this.blockedUserId = blockedUserId;
    this.isBlocked = isBlocked;
  }

  public UserBlock toDomain() {
    return new UserBlock(
        id, blockerUserId, blockedUserId, isBlocked, getCreatedAt(), getUpdatedAt());
  }

  public static UserBlockEntity fromDomain(UserBlock userBlock) {
    return UserBlockEntity.builder()
        .id(userBlock.id())
        .blockerUserId(userBlock.blockerUserId())
        .blockedUserId(userBlock.blockedUserId())
        .isBlocked(userBlock.isBlocked())
        .build();
  }
}
