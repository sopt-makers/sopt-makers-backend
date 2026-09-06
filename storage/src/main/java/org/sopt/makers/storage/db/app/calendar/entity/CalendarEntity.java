package org.sopt.makers.storage.db.app.calendar.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.app.calendar.Calendar;
import org.sopt.makers.domain.app.calendar.CalendarType;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "calendar")
public class CalendarEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "generation", nullable = false)
  private Integer generation;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "is_one_day_schedule", nullable = false)
  private Boolean isOneDaySchedule;

  @Column(name = "is_only_active_generation", nullable = false)
  private Boolean isOnlyActiveGeneration;

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private CalendarType type;

  public Calendar toDomain() {
    return new Calendar(
        id, generation, title, isOneDaySchedule, isOnlyActiveGeneration, startDate, endDate, type);
  }
}
