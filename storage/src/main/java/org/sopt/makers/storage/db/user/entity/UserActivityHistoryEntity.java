package org.sopt.makers.storage.db.user.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.Role;
import org.sopt.makers.domain.user.Team;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "user_activity_histories",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "UK_USER_ID_AND_GENERATION",
          columnNames = {"user_id", "generation", "is_sopt"})
    })
public class UserActivityHistoryEntity extends BaseEntity {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(value = PROTECTED)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  private int generation;

  @Enumerated(EnumType.STRING)
  private Team team;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Part part;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Role role;

  @Column(name = "is_sopt", nullable = false)
  private boolean isSopt;

  @Column(name = "attendance_score")
  private Float attendanceScore;

  @Builder(access = PRIVATE)
  private UserActivityHistoryEntity(
      UserEntity user,
      int generation,
      Team team,
      Part part,
      Role role,
      boolean isSopt,
      Float attendanceScore) {
    this.user = user;
    this.generation = generation;
    this.team = team;
    this.part = part;
    this.role = role;
    this.isSopt = isSopt;
    this.attendanceScore = attendanceScore;
  }

  public static UserActivityHistoryEntity create(
      UserEntity user,
      int generation,
      Team team,
      Part part,
      Role role,
      boolean isSopt,
      Float attendanceScore) {
    return UserActivityHistoryEntity.builder()
        .user(user)
        .generation(generation)
        .team(team)
        .part(part)
        .role(role)
        .isSopt(isSopt)
        .attendanceScore(attendanceScore)
        .build();
  }

  public Activity toDomain() {
    return Activity.of(getId(), generation, team, part, role, isSopt, attendanceScore);
  }
}
