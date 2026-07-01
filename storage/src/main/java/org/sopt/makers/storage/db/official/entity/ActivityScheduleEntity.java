package org.sopt.makers.storage.db.official.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.official.activityschedule.ActivitySchedule;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "activity_schedule")
public class ActivityScheduleEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "generation_id", nullable = false)
  private Integer generationId;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "end_date")
  private LocalDate endDate;

  @Builder(access = PRIVATE)
  private ActivityScheduleEntity(
      Long id, Integer generationId, String name, LocalDate startDate, LocalDate endDate) {
    this.id = id;
    this.generationId = generationId;
    this.name = name;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public ActivitySchedule toDomain() {
    return new ActivitySchedule(id, generationId, name, startDate, endDate);
  }

  public static ActivityScheduleEntity fromDomain(ActivitySchedule activitySchedule) {
    return ActivityScheduleEntity.builder()
        .id(activitySchedule.id())
        .generationId(activitySchedule.generationId())
        .name(activitySchedule.name())
        .startDate(activitySchedule.startDate())
        .endDate(activitySchedule.endDate())
        .build();
  }
}
