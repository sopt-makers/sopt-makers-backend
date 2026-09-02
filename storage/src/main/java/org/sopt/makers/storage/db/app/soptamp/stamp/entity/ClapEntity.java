package org.sopt.makers.storage.db.app.soptamp.stamp.entity;

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
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(
    name = "clap",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_clap_stamp_id_user_id",
            columnNames = {"stamp_id", "user_id"}))
public class ClapEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "stamp_id", nullable = false)
  private Long stampId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "clap_count", nullable = false)
  private int clapCount;
}
