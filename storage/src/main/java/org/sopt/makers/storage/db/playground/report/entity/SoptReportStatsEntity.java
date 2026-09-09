package org.sopt.makers.storage.db.playground.report.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.sopt.makers.domain.playground.report.SoptReportStats;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Immutable
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "sopt_report_stats")
public class SoptReportStatsEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "template_key", nullable = false, unique = true)
  private String templateKey;

  @Column(name = "data", nullable = false, columnDefinition = "JSON")
  private String data;

  @Column(name = "category")
  private String category;

  public SoptReportStats toDomain() {
    return new SoptReportStats(id, templateKey, data, category);
  }
}
