package org.sopt.makers.domain.app.calendar.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.sopt.makers.domain.app.calendar.exception.CalendarFailure.NOT_FOUND_CALENDAR;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.app.calendar.Calendar;
import org.sopt.makers.domain.app.calendar.CalendarType;
import org.sopt.makers.domain.app.calendar.exception.CalendarException;
import org.sopt.makers.domain.app.calendar.port.CalendarCacheRepositoryPort;
import org.sopt.makers.domain.app.calendar.port.CalendarRepositoryPort;

@DisplayName("CalendarService 테스트")
class CalendarServiceTest {

  private static final int CURRENT_GENERATION = 38;
  private static final LocalDate TODAY = LocalDate.of(2026, 9, 3);

  private InMemoryCalendarRepositoryPort calendarRepositoryPort;
  private InMemoryCalendarCacheRepositoryPort calendarCacheRepositoryPort;
  private CalendarService calendarService;

  @BeforeEach
  void setUp() {
    calendarRepositoryPort = new InMemoryCalendarRepositoryPort();
    calendarCacheRepositoryPort = new InMemoryCalendarCacheRepositoryPort();
    Clock clock = Clock.fixed(Instant.parse("2026-09-03T03:00:00Z"), ZoneOffset.UTC);
    calendarService =
        new CalendarService(
            calendarRepositoryPort, calendarCacheRepositoryPort, clock, CURRENT_GENERATION);
  }

  @Test
  @DisplayName("캐시가 있으면 DB 를 부르지 않는다")
  void usesCacheWhenPresent() {
    calendarCacheRepositoryPort.save(CURRENT_GENERATION, List.of(calendar(1L, "9월 1일", 9, 1)));
    calendarRepositoryPort.add(calendar(2L, "DB 에만", 9, 2));

    List<Calendar> result = calendarService.getAllCurrentGenerationCalendar();

    assertThat(result).extracting(Calendar::title).containsExactly("9월 1일");
    assertThat(calendarRepositoryPort.calls).isZero();
  }

  @Test
  @DisplayName("캐시가 없으면 현재 기수 일정을 DB 에서 읽고 캐시에 저장한다")
  void loadsFromDbAndCaches() {
    calendarRepositoryPort.add(calendar(1L, "세미나", 9, 5));
    calendarRepositoryPort.add(
        new Calendar(9L, 37, "지난 기수", true, false, TODAY, TODAY, CalendarType.ETC));

    List<Calendar> result = calendarService.getAllCurrentGenerationCalendar();

    assertThat(result).extracting(Calendar::title).containsExactly("세미나");
    assertThat(calendarCacheRepositoryPort.findByGeneration(CURRENT_GENERATION)).contains(result);
  }

  @Test
  @DisplayName("오늘 이후 시작하는 첫 일정이 recent 다. 오늘 시작은 포함, 진행 중인 과거 시작은 제외")
  void picksFirstUpcoming() {
    Calendar ongoing =
        new Calendar(
            1L,
            38,
            "진행 중",
            false,
            false,
            TODAY.minusDays(1),
            TODAY.plusDays(3),
            CalendarType.EVENT);
    Calendar today = calendar(2L, "오늘", 9, 3);
    Calendar later = calendar(3L, "다음 주", 9, 10);

    Optional<Calendar> recent = calendarService.getRecentCalendar(List.of(later, ongoing, today));

    assertThat(recent).contains(today);
  }

  @Test
  @DisplayName("다가오는 일정이 없으면 마지막 일정을 준다")
  void fallsBackToLast() {
    calendarRepositoryPort.add(calendar(1L, "지난 일정", 8, 1));
    calendarRepositoryPort.add(calendar(2L, "마지막 일정", 8, 20));

    assertThat(calendarService.getRecentCalendarOrLast().title()).isEqualTo("마지막 일정");
  }

  @Test
  @DisplayName("일정이 하나도 없으면 NOT_FOUND_CALENDAR 예외가 발생한다")
  void throwsWhenEmpty() {
    assertThatThrownBy(() -> calendarService.getRecentCalendarOrLast())
        .isInstanceOf(CalendarException.class)
        .extracting("error")
        .isEqualTo(NOT_FOUND_CALENDAR);
  }

  private static Calendar calendar(Long id, String title, int month, int day) {
    LocalDate date = LocalDate.of(2026, month, day);
    return new Calendar(
        id, CURRENT_GENERATION, title, true, false, date, date, CalendarType.SEMINAR);
  }

  private static final class InMemoryCalendarRepositoryPort implements CalendarRepositoryPort {
    private final List<Calendar> store = new java.util.ArrayList<>();
    private int calls;

    void add(Calendar calendar) {
      store.add(calendar);
    }

    @Override
    public List<Calendar> findAllByGenerationOrderByStartDateAscEndDateAsc(int generation) {
      calls++;
      return store.stream()
          .filter(c -> c.generation() == generation)
          .sorted(
              java.util.Comparator.comparing(Calendar::startDate).thenComparing(Calendar::endDate))
          .toList();
    }
  }

  private static final class InMemoryCalendarCacheRepositoryPort
      implements CalendarCacheRepositoryPort {
    private final Map<Integer, List<Calendar>> store = new HashMap<>();

    @Override
    public Optional<List<Calendar>> findByGeneration(int generation) {
      return Optional.ofNullable(store.get(generation));
    }

    @Override
    public void save(int generation, List<Calendar> calendars) {
      store.put(generation, calendars);
    }
  }
}
