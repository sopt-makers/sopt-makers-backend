package org.sopt.makers.domain.official.activityschedule.port;

import java.util.List;
import org.sopt.makers.domain.official.activityschedule.ActivitySchedule;

public interface ActivityScheduleRepositoryPort {

  List<ActivitySchedule> saveAll(List<ActivitySchedule> activitySchedules);

  void deleteByGenerationId(Integer generationId);

  List<ActivitySchedule> findByGenerationIdOrderByStartDateAsc(Integer generationId);
}
