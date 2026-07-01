package org.sopt.makers.storage.db.official.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleEmbeddable {

  @Column(name = "application_start_time", nullable = false, length = 50)
  public String applicationStartTime;

  @Column(name = "application_end_time", nullable = false, length = 50)
  public String applicationEndTime;

  @Column(name = "application_result_time", nullable = false, length = 50)
  public String applicationResultTime;

  @Column(name = "interview_start_time", nullable = false, length = 50)
  public String interviewStartTime;

  @Column(name = "interview_end_time", nullable = false, length = 50)
  public String interviewEndTime;

  @Column(name = "final_result_time", nullable = false, length = 50)
  public String finalResultTime;
}
