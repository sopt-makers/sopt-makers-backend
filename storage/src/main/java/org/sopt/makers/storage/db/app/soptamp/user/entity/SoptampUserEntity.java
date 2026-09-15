package org.sopt.makers.storage.db.app.soptamp.user.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.app.soptamp.SoptampPart;
import org.sopt.makers.domain.app.soptamp.SoptampUser;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "soptamp_user",
    uniqueConstraints = {
      @UniqueConstraint(name = "uk_soptamp_user_user_id", columnNames = "user_id"),
      @UniqueConstraint(name = "uk_soptamp_user_nickname", columnNames = "nickname")
    },
    indexes = @Index(name = "idx_soptamp_user_generation", columnList = "generation"))
public class SoptampUserEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "nickname")
  private String nickname;

  @Column(name = "total_points")
  private Long totalPoints;

  @Column(name = "profile_message", columnDefinition = "TEXT")
  private String profileMessage;

  @Column(name = "generation")
  private Long generation;

  @Enumerated(EnumType.STRING)
  @Column(name = "part")
  private SoptampPart part;

  private SoptampUserEntity(SoptampUser soptampUser) {
    this.userId = soptampUser.userId();
    this.nickname = soptampUser.nickname();
    this.totalPoints = soptampUser.totalPoints();
    this.profileMessage = soptampUser.profileMessage() == null ? "" : soptampUser.profileMessage();
    this.generation = soptampUser.generation();
    this.part = soptampUser.part();
  }

  public static SoptampUserEntity from(SoptampUser soptampUser) {
    return new SoptampUserEntity(soptampUser);
  }

  public void apply(SoptampUser soptampUser) {
    this.nickname = soptampUser.nickname();
    this.totalPoints = soptampUser.totalPoints();
    this.profileMessage = soptampUser.profileMessage() == null ? "" : soptampUser.profileMessage();
    this.generation = soptampUser.generation();
    this.part = soptampUser.part();
  }

  public void addPointsByLevel(int level) {
    this.totalPoints = (this.totalPoints == null ? 0L : this.totalPoints) + level;
  }

  public void subtractPointsByLevel(int level) {
    addPointsByLevel(-level);
  }

  public void initTotalPoints() {
    this.totalPoints = 0L;
  }

  public SoptampUser toDomain() {
    return new SoptampUser(id, userId, profileMessage, totalPoints, nickname, generation, part);
  }
}
