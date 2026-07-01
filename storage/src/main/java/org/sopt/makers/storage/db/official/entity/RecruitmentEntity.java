package org.sopt.makers.storage.db.official.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.official.recruitment.RecruitType;
import org.sopt.makers.domain.official.recruitment.Recruitment;
import org.sopt.makers.domain.official.recruitment.Schedule;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "recruitment")
public class RecruitmentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "generation_id", nullable = false)
  private Integer generationId;

  @Enumerated(EnumType.STRING)
  @Column(name = "recruit_type", nullable = false, length = 10)
  private RecruitType recruitType;

  @Embedded private ScheduleEmbeddable schedule;

  @Builder(access = PRIVATE)
  private RecruitmentEntity(
      Long id, Integer generationId, RecruitType recruitType, ScheduleEmbeddable schedule) {
    this.id = id;
    this.generationId = generationId;
    this.recruitType = recruitType;
    this.schedule = schedule;
  }

  public Recruitment toDomain() {
    Schedule s =
        schedule != null
            ? new Schedule(
                schedule.applicationStartTime,
                schedule.applicationEndTime,
                schedule.applicationResultTime,
                schedule.interviewStartTime,
                schedule.interviewEndTime,
                schedule.finalResultTime)
            : null;
    return new Recruitment(id, generationId, recruitType, s);
  }

  public static RecruitmentEntity fromDomain(Recruitment recruitment) {
    ScheduleEmbeddable s =
        recruitment.schedule() != null
            ? new ScheduleEmbeddable(
                recruitment.schedule().applicationStartTime(),
                recruitment.schedule().applicationEndTime(),
                recruitment.schedule().applicationResultTime(),
                recruitment.schedule().interviewStartTime(),
                recruitment.schedule().interviewEndTime(),
                recruitment.schedule().finalResultTime())
            : null;
    return RecruitmentEntity.builder()
        .id(recruitment.id())
        .generationId(recruitment.generationId())
        .recruitType(recruitment.recruitType())
        .schedule(s)
        .build();
  }
}
