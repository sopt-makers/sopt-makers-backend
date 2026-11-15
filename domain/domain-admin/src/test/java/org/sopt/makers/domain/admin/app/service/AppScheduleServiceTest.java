package org.sopt.makers.domain.admin.app.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.admin.app.AppSchedule;
import org.sopt.makers.domain.admin.app.exception.AppException;
import org.sopt.makers.domain.admin.app.port.AppScheduleRepositoryPort;
import org.sopt.makers.domain.admin.lecture.LectureAttribute;

class AppScheduleServiceTest {
  private final AppScheduleRepositoryPort port = mock(AppScheduleRepositoryPort.class);
  private final AppScheduleService service = new AppScheduleService(port);
  private final LocalDateTime start = LocalDateTime.of(2026, 11, 1, 14, 0);

  @Test
  @DisplayName("일정이 없어도 조회한 50일의 날짜를 반환한다")
  void acceptsFiftyDays() {
    when(port.findBetween(start, start.plusDays(49))).thenReturn(List.of());
    var result = service.getSchedules(start, start.plusDays(49));
    assertThat(result).hasSize(50);
    assertThat(result.values()).allSatisfy(list -> assertThat(list).isEmpty());
  }

  @Test
  @DisplayName("최대 조회 기간을 초과하면 예외가 발생한다")
  void rejectsLongRange() {
    when(port.findBetween(start, start.plusDays(50))).thenReturn(List.of());
    assertThatThrownBy(() -> service.getSchedules(start, start.plusDays(50)))
        .isInstanceOf(AppException.class);
  }

  @Test
  @DisplayName("조회 날짜 수는 두 시각 사이의 경과 일수로 계산한다")
  void countsElapsedDays() {
    when(port.findBetween(start, start.plusHours(12))).thenReturn(List.of());
    assertThat(service.getSchedules(start, start.plusHours(12)))
        .containsOnlyKeys(start.toLocalDate());
  }

  @Test
  @DisplayName("여러 날 일정은 시작일과 중간 날짜 및 종료일에 포함한다")
  void expandsMultiDaySchedule() {
    var schedule =
        new AppSchedule(1L, start.minusDays(1), start.plusDays(2), LectureAttribute.EVENT, "행사");
    when(port.findBetween(start, start.plusDays(2))).thenReturn(List.of(schedule));
    var result = service.getSchedules(start, start.plusDays(2));
    assertThat(result).hasSize(4);
    assertThat(result.values()).allSatisfy(list -> assertThat(list).containsExactly(schedule));
  }

  @Test
  @DisplayName("24시간 미만 일정은 날짜가 바뀌어도 시작일에만 포함한다")
  void keepsShortScheduleOnStartDate() {
    var schedule = new AppSchedule(1L, start, start.plusHours(12), LectureAttribute.EVENT, "행사");
    when(port.findBetween(start, start.plusDays(1))).thenReturn(List.of(schedule));
    var result = service.getSchedules(start, start.plusDays(1));
    assertThat(result.get(start.toLocalDate())).containsExactly(schedule);
    assertThat(result.get(start.plusDays(1).toLocalDate())).isEmpty();
  }
}
