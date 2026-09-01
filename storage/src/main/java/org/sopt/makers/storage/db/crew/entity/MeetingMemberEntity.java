package org.sopt.makers.storage.db.crew.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.crew.meeting.Member;
import org.sopt.makers.domain.crew.meeting.MemberRole;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "meeting_member",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_meeting_member_meeting_user",
            columnNames = {"meeting_id", "user_id"}))
public class MeetingMemberEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "meeting_id", nullable = false)
  private Long meetingId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MemberRole role;

  @Builder(access = PRIVATE)
  private MeetingMemberEntity(Long id, Long meetingId, Long userId, MemberRole role) {
    this.id = id;
    this.meetingId = meetingId;
    this.userId = userId;
    this.role = role;
  }

  public Member toDomain() {
    return new Member(meetingId, userId, role);
  }

  public void replaceRole(MemberRole role) {
    this.role = role;
  }

  public static MeetingMemberEntity fromDomain(Member member) {
    return MeetingMemberEntity.builder()
        .meetingId(member.meetingId())
        .userId(member.userId())
        .role(member.role())
        .build();
  }
}
