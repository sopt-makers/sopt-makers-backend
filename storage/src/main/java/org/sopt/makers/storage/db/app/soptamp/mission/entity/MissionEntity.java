package org.sopt.makers.storage.db.app.soptamp.mission.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.sopt.makers.domain.app.soptamp.mission.Mission;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "mission")
public class MissionEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "mission_id", nullable = false)
  private Long id;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "level", nullable = false)
  private Integer level;

  @Column(name = "display", nullable = false)
  private boolean display;

  @JdbcTypeCode(SqlTypes.ARRAY)
  @Column(name = "profile_image", columnDefinition = "text[]")
  private List<String> profileImages;

  private MissionEntity(Mission mission) {
    this.title = mission.title();
    this.level = mission.level();
    this.display = mission.display();
    this.profileImages = mission.profileImages();
  }

  public static MissionEntity from(Mission mission) {
    return new MissionEntity(mission);
  }

  public Mission toDomain() {
    return new Mission(id, title, level, display, profileImages);
  }
}
