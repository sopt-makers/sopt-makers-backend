package org.sopt.makers.domain.app.calendar.service;

import static org.sopt.makers.domain.app.calendar.exception.CalendarFailure.NOT_FOUND_CALENDAR;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.calendar.Calendar;
import org.sopt.makers.domain.app.calendar.exception.CalendarException;
import org.sopt.makers.domain.app.calendar.port.CalendarCacheRepositoryPort;
import org.sopt.makers.domain.app.calendar.port.CalendarRepositoryPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CalendarService {

  private static final ZoneId KST = ZoneId.of("Asia/Seoul");

  private final CalendarRepositoryPort calendarRepositoryPort;
  private final CalendarCacheRepositoryPort calendarCacheRepositoryPort;
  private final Clock clock;
  private final int currentGeneration;

  public CalendarService(
      CalendarRepositoryPort calendarRepositoryPort,
      CalendarCacheRepositoryPort calendarCacheRepositoryPort,
      Clock clock,
      @Value("${sopt.current.generation}") int currentGeneration) {
    this.calendarRepositoryPort = calendarRepositoryPort;
    this.calendarCacheRepositoryPort = calendarCacheRepositoryPort;
    this.clock = clock;
    this.currentGeneration = currentGeneration;
  }

  public List<Calendar> getAllCurrentGenerationCalendar() {
    return calendarCacheRepositoryPort
        .findByGeneration(currentGeneration)
        .orElseGet(this::cacheAllCalendar);
  }

  public Optional<Calendar> getRecentCalendar(List<Calendar> calendars) {
    LocalDate today = LocalDate.now(clock.withZone(KST));
    return calendars.stream()
        .sorted(Comparator.comparing(Calendar::startDate).thenComparing(Calendar::endDate))
        .filter(calendar -> !calendar.startDate().isBefore(today))
        .findFirst();
  }

  public Calendar getRecentCalendarOrLast() {
    List<Calendar> calendars = getAllCurrentGenerationCalendar();
    if (calendars.isEmpty()) {
      throw new CalendarException(NOT_FOUND_CALENDAR);
    }
    return getRecentCalendar(calendars).orElseGet(calendars::getLast);
  }

  private List<Calendar> cacheAllCalendar() {
    List<Calendar> calendars =
        calendarRepositoryPort.findAllByGenerationOrderByStartDateAscEndDateAsc(currentGeneration);
    calendarCacheRepositoryPort.save(currentGeneration, calendars);
    return calendars;
  }
}
