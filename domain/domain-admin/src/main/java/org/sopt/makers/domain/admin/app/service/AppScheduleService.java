package org.sopt.makers.domain.admin.app.service;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.admin.app.AppSchedule;
import org.sopt.makers.domain.admin.app.exception.AppException;
import org.sopt.makers.domain.admin.app.exception.AppFailure;
import org.sopt.makers.domain.admin.app.port.AppScheduleRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppScheduleService {

  private static final int MIN_SCHEDULE_DURATION = 1;
  private static final int MAX_SCHEDULE_DURATION = 31;
  private static final int DAY_DURATION = 1;
  private static final int TWO_DAYS_DURATION = 2;

  private final AppScheduleRepositoryPort appScheduleRepositoryPort;

  public Map<LocalDate, List<AppSchedule>> getSchedules(
      LocalDateTime startAt, LocalDateTime endAt) {
    List<AppSchedule> schedules = appScheduleRepositoryPort.findBetween(startAt, endAt);
    Map<LocalDate, List<AppSchedule>> scheduleMap = getInitializedMap(startAt, endAt);
    schedules.forEach(schedule -> putScheduleToMap(scheduleMap, schedule));
    return scheduleMap;
  }

  private Map<LocalDate, List<AppSchedule>> getInitializedMap(
      LocalDateTime startAt, LocalDateTime endAt) {
    int duration = getDuration(startAt, endAt);
    return IntStream.range(0, duration)
        .mapToObj(startAt::plusDays)
        .collect(Collectors.toMap(LocalDateTime::toLocalDate, date -> new ArrayList<>()));
  }

  private int getDuration(LocalDateTime startAt, LocalDateTime endAt) {
    long duration = DAYS.between(startAt.toLocalDate(), endAt.toLocalDate()) + 1;
    if (duration < MIN_SCHEDULE_DURATION || duration > MAX_SCHEDULE_DURATION) {
      throw new AppException(AppFailure.INVALID_SCHEDULE_DATE_RANGE);
    }
    return (int) duration;
  }

  private void putScheduleToMap(
      Map<LocalDate, List<AppSchedule>> scheduleMap, AppSchedule schedule) {
    long duration = DAYS.between(schedule.startAt().toLocalDate(), schedule.endAt().toLocalDate());
    scheduleMap
        .computeIfAbsent(schedule.startAt().toLocalDate(), key -> new ArrayList<>())
        .add(schedule);

    if (duration >= DAY_DURATION) {
      scheduleMap
          .computeIfAbsent(schedule.endAt().toLocalDate(), key -> new ArrayList<>())
          .add(schedule);
      if (duration >= TWO_DAYS_DURATION) {
        putScheduleMapBetween(scheduleMap, schedule, (int) duration);
      }
    }
  }

  private void putScheduleMapBetween(
      Map<LocalDate, List<AppSchedule>> scheduleMap, AppSchedule schedule, int duration) {
    IntStream.range(1, duration).forEach(day -> putScheduleAtDayCount(scheduleMap, schedule, day));
  }

  private void putScheduleAtDayCount(
      Map<LocalDate, List<AppSchedule>> scheduleMap, AppSchedule schedule, int dayCount) {
    LocalDate date = schedule.startAt().plusDays(dayCount).toLocalDate();
    scheduleMap.computeIfAbsent(date, key -> new ArrayList<>()).add(schedule);
  }
}
