package org.sopt.makers.storage.db.app.soptamp.appjam.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.app.soptamp.appjam.AppjamUser;
import org.sopt.makers.domain.app.soptamp.appjam.TeamNumber;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "appjam_user")
public class AppjamUserEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "team_name", nullable = false)
  private String teamName;

  @Enumerated(EnumType.STRING)
  @Column(name = "team_number", nullable = false)
  private TeamNumber teamNumber;

  public AppjamUser toDomain() {
    return new AppjamUser(id, userId, teamName, teamNumber);
  }
}
