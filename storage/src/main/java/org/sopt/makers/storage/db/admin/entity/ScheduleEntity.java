package org.sopt.makers.storage.db.admin.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sopt.makers.domain.admin.app.AppSchedule;
import org.sopt.makers.domain.admin.lecture.LectureAttribute;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "schedules")
public class ScheduleEntity extends BaseEntity {

  @Id
  @Column(name = "schedule_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(value = PROTECTED)
  private Long id;

  private LocalDateTime startDate;

  private LocalDateTime endDate;

  @Enumerated(EnumType.STRING)
  private LectureAttribute attribute;

  private String title;

  public AppSchedule toDomain() {
    return new AppSchedule(getId(), startDate, endDate, attribute, title);
  }
}
