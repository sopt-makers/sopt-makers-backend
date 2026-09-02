package org.sopt.makers.storage.db.app.soptamp.stamp.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.sopt.makers.domain.app.soptamp.stamp.Stamp;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "stamp",
    indexes = {
      @Index(name = "idx_stamp_user_id_mission_id", columnList = "user_id, mission_id"),
      @Index(name = "idx_stamp_mission_id", columnList = "mission_id")
    })
public class StampEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "contents", nullable = false, columnDefinition = "TEXT")
  private String contents;

  @JdbcTypeCode(SqlTypes.ARRAY)
  @Column(name = "images", columnDefinition = "text[]")
  private List<String> images;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "mission_id", nullable = false)
  private Long missionId;

  @Column(name = "activity_date", length = 10)
  private String activityDate;

  @Column(name = "clap_count", nullable = false)
  private int clapCount;

  @Column(name = "view_count", nullable = false)
  private int viewCount;

  @Version
  @Column(name = "version")
  private Long version;

  private StampEntity(Stamp stamp) {
    this.contents = stamp.contents();
    this.images = stamp.images();
    this.userId = stamp.userId();
    this.missionId = stamp.missionId();
    this.activityDate = stamp.activityDate();
    this.clapCount = stamp.clapCount();
    this.viewCount = stamp.viewCount();
  }

  public static StampEntity from(Stamp stamp) {
    return new StampEntity(stamp);
  }

  public void applyEdit(String contents, List<String> images, String activityDate) {
    this.contents = contents;
    this.images = images;
    this.activityDate = activityDate;
  }

  public Stamp toDomain() {
    return new Stamp(
        id,
        contents,
        images,
        userId,
        missionId,
        activityDate,
        getCreatedAt(),
        getUpdatedAt(),
        clapCount,
        viewCount,
        version);
  }
}
