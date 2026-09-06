package org.sopt.makers.storage.redis.app.calendar.cache;

import java.time.LocalDate;
import java.util.List;
import org.sopt.makers.domain.app.calendar.Calendar;
import org.sopt.makers.domain.app.calendar.CalendarType;

public record CachedCalendars(List<CachedCalendar> calendars) {

  public static CachedCalendars from(List<Calendar> calendars) {
    return new CachedCalendars(calendars.stream().map(CachedCalendar::from).toList());
  }

  public List<Calendar> toDomain() {
    return calendars.stream().map(CachedCalendar::toDomain).toList();
  }

  public record CachedCalendar(
      Long id,
      int generation,
      String title,
      boolean isOneDaySchedule,
      boolean isOnlyActiveGeneration,
      LocalDate startDate,
      LocalDate endDate,
      CalendarType type) {

    static CachedCalendar from(Calendar calendar) {
      return new CachedCalendar(
          calendar.id(),
          calendar.generation(),
          calendar.title(),
          calendar.isOneDaySchedule(),
          calendar.isOnlyActiveGeneration(),
          calendar.startDate(),
          calendar.endDate(),
          calendar.type());
    }

    Calendar toDomain() {
      return new Calendar(
          id,
          generation,
          title,
          isOneDaySchedule,
          isOnlyActiveGeneration,
          startDate,
          endDate,
          type);
    }
  }
}
