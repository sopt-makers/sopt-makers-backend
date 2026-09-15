package org.sopt.makers.storage.db.playground.report.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "amplitude_event_raw_data")
public class AmplitudeEventRawDataEntity {

  @Id
  @Column(name = "\"$insert_id\"")
  private String insertId;

  @Column(name = "user_id")
  private String userId;

  @Column(name = "event_type")
  private String eventType;

  @Column(name = "event_time")
  private String eventTime;

  @Column(name = "\"event_properties_[Amplitude] Page Path\"")
  private String eventPropertiesPagePath;
}
