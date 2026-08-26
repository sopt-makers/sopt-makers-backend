package org.sopt.makers.storage.db.app.soptletter.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.app.soptletter.SoptLetterProfile;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "sopt_letter_profile",
    uniqueConstraints = {
      @UniqueConstraint(name = "uk_sopt_letter_profile_user", columnNames = "user_id"),
      @UniqueConstraint(name = "uk_sopt_letter_profile_nickname", columnNames = "nickname")
    })
public class SoptLetterProfileEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "nickname", nullable = false)
  private String nickname;

  @Column(name = "is_onboarded", nullable = false)
  private boolean isOnboarded;

  private SoptLetterProfileEntity(SoptLetterProfile profile) {
    this.userId = profile.userId();
    this.nickname = profile.nickname();
    this.isOnboarded = profile.isOnboarded();
  }

  public static SoptLetterProfileEntity from(SoptLetterProfile profile) {
    return new SoptLetterProfileEntity(profile);
  }

  public SoptLetterProfile toDomain() {
    return new SoptLetterProfile(id, userId, nickname, isOnboarded);
  }
}
