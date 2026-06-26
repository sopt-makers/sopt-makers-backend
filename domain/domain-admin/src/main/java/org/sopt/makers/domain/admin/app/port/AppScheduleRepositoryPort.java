package org.sopt.makers.domain.admin.app.port;

import java.time.LocalDateTime;
import java.util.List;
import org.sopt.makers.domain.admin.app.AppSchedule;

public interface AppScheduleRepositoryPort {

  List<AppSchedule> findBetween(LocalDateTime startAt, LocalDateTime endAt);
}
