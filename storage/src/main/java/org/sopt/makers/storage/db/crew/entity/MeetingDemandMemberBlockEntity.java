package org.sopt.makers.storage.db.crew.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Entity
@Getter
@Immutable
@NoArgsConstructor(access = PROTECTED)
@Table(name = "member_block")
public class MeetingDemandMemberBlockEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "blocker_id", nullable = false)
  private Long blockerId;

  @Column(name = "blocked_member_id", nullable = false)
  private Long blockedMemberId;

  @Column(name = "is_blocked", nullable = false)
  private Boolean isBlocked;
}
