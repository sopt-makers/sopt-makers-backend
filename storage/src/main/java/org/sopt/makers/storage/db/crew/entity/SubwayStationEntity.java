package org.sopt.makers.storage.db.crew.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.crew.soptmap.SubwayLine;
import org.sopt.makers.domain.crew.soptmap.SubwayStation;
import org.sopt.makers.storage.db.common.BaseEntity;
import org.sopt.makers.storage.db.crew.converter.SubwayLineListConverter;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "subway_station")
public class SubwayStationEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @Convert(converter = SubwayLineListConverter.class)
  @Column(nullable = false, columnDefinition = "TEXT")
  private List<SubwayLine> lines;

  public SubwayStation toDomain() {
    return new SubwayStation(id, name, lines, getCreatedAt(), getUpdatedAt());
  }
}
