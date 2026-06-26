package org.sopt.makers.storage.db.admin.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.admin.app.AppSchedule;
import org.sopt.makers.domain.admin.attendance.LectureAttribute;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "schedules")
public class ScheduleEntity extends BaseEntity {

  private LocalDateTime startDate;

  private LocalDateTime endDate;

  @Enumerated(EnumType.STRING)
  private LectureAttribute attribute;

  private String title;

  public AppSchedule toDomain() {
    return new AppSchedule(getId(), startDate, endDate, attribute, title);
  }
}
