package org.sopt.makers.domain.official.activityschedule.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.activityschedule.ActivitySchedule;
import org.sopt.makers.domain.official.activityschedule.port.ActivityScheduleRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityScheduleService {

  private final ActivityScheduleRepositoryPort activityScheduleRepositoryPort;

  @Transactional
  public void bulkCreate(BulkCreateActivitySchedulesCommand command) {
    activityScheduleRepositoryPort.deleteByGenerationId(command.generationId());

    List<ActivitySchedule> schedules =
        command.activitySchedules().stream()
            .map(
                data ->
                    new ActivitySchedule(
                        null,
                        command.generationId(),
                        data.name(),
                        data.startDate(),
                        data.endDate()))
            .toList();

    activityScheduleRepositoryPort.saveAll(schedules);
  }

  public List<ActivitySchedule> findByGeneration(Integer generationId) {
    return activityScheduleRepositoryPort.findByGenerationIdOrderByStartDateAsc(generationId);
  }

  public record BulkCreateActivitySchedulesCommand(
      Integer generationId, List<ActivityScheduleData> activitySchedules) {

    public record ActivityScheduleData(String name, LocalDate startDate, LocalDate endDate) {}
  }
}
