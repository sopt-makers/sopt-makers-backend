package org.sopt.makers.storage.db.app.soptamp.clap.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.app.soptamp.clap.Clap;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Table(
    name = "clap",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_clap_stamp_user",
            columnNames = {"stamp_id", "user_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ClapEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "stamp_id", nullable = false)
  private Long stampId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "clap_count", nullable = false)
  private int clapCount;

  @Version private Long version;

  public static ClapEntity from(Clap clap) {
    return ClapEntity.builder()
        .id(clap.id())
        .stampId(clap.stampId())
        .userId(clap.userId())
        .clapCount(clap.clapCount())
        .version(clap.version())
        .build();
  }

  public Clap toDomain() {
    return new Clap(id, stampId, userId, clapCount, version);
  }

  public void changeClapCount(int clapCount) {
    this.clapCount = clapCount;
  }
}
