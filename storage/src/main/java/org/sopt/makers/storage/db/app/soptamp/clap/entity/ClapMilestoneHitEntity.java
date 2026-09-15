package org.sopt.makers.storage.db.app.soptamp.clap.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clap_milestone_hit")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClapMilestoneHitEntity {

  @EmbeddedId private Id id;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Embeddable
  @Getter
  @EqualsAndHashCode
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  public static class Id implements Serializable {

    @Column(name = "stamp_id", nullable = false)
    private Long stampId;

    @Column(name = "milestone", nullable = false)
    private Integer milestone;
  }
}
