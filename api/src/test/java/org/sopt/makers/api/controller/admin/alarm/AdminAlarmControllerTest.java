package org.sopt.makers.api.controller.admin.alarm;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.sopt.makers.api.common.exception.GlobalExceptionHandler;
import org.sopt.makers.domain.admin.alarm.*;
import org.sopt.makers.domain.admin.alarm.service.AlarmService;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AdminAlarmControllerTest {
  private static final String BASE = "/api/v1/alarms";
  private final AlarmService service = mock(AlarmService.class);
  private final MockMvc mvc =
      MockMvcBuilders.standaloneSetup(new AdminAlarmController(service))
          .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
          .setControllerAdvice(new GlobalExceptionHandler())
          .build();
  private static final String CONTENT =
      """
      "title":"공지","content":"내용","category":"NOTICE","targetType":"ALL",
      "part":"ALL","createdGeneration":39,"linkType":"NONE"
      """;

  @Test
  @DisplayName("알림을 즉시 발송하고 생성한 ID를 반환한다")
  void sendsInstantAlarm() throws Exception {
    when(service.sendInstantAlarm(any())).thenReturn(1L);
    mvc.perform(
            post(BASE + "/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" + CONTENT + "}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.id").value(1));
    verify(service)
        .sendInstantAlarm(
            new AlarmService.SendInstantAlarmCommand(
                "공지",
                "내용",
                AlarmCategory.NOTICE,
                AlarmTargetType.ALL,
                AlarmTargetPart.ALL,
                39,
                null,
                AlarmLinkType.NONE,
                null));
  }

  @Test
  @DisplayName("지정한 날짜와 시간으로 알림을 예약한다")
  void schedulesAlarm() throws Exception {
    when(service.sendScheduleAlarm(any())).thenReturn(2L);
    mvc.perform(
            post(BASE + "/schedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" + CONTENT + ",\"postDate\":\"2026-11-15\",\"postTime\":\"14:00\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.id").value(2));
    verify(service)
        .sendScheduleAlarm(
            new AlarmService.SendScheduleAlarmCommand(
                "공지",
                "내용",
                AlarmCategory.NOTICE,
                AlarmTargetType.ALL,
                null,
                AlarmTargetPart.ALL,
                39,
                AlarmLinkType.NONE,
                null,
                "2026-11-15",
                "14:00"));
  }

  @Test
  @DisplayName("조건 없이 조회하면 첫 페이지의 알림 20개를 요청한다")
  void getsFirstPage() throws Exception {
    when(service.getAlarms(null, null, 0, 20))
        .thenReturn(new AlarmService.AlarmListResult(List.of(), 0));
    mvc.perform(get(BASE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.alarms").isEmpty())
        .andExpect(jsonPath("$.data.totalCount").value(0));
    verify(service).getAlarms(null, null, 0, 20);
  }

  @Test
  @DisplayName("기수와 상태 및 페이지 조건으로 알림을 조회한다")
  void getsFilteredPage() throws Exception {
    when(service.getAlarms(39, AlarmStatus.SCHEDULED, 2, 5))
        .thenReturn(new AlarmService.AlarmListResult(List.of(), 0));
    mvc.perform(
            get(BASE)
                .param("generation", "39")
                .param("status", "SCHEDULED")
                .param("page", "2")
                .param("size", "5"))
        .andExpect(status().isOk());
    verify(service).getAlarms(39, AlarmStatus.SCHEDULED, 2, 5);
  }

  @ParameterizedTest
  @CsvSource({"-1,0,0,20", "0,3000,0,2000", "invalid,invalid,0,20"})
  @DisplayName("페이지 범위와 잘못된 입력은 기본값 및 최대 크기로 보정한다")
  void normalizesPage(String page, String size, int expectedPage, int expectedSize)
      throws Exception {
    when(service.getAlarms(null, null, expectedPage, expectedSize))
        .thenReturn(new AlarmService.AlarmListResult(List.of(), 0));
    mvc.perform(get(BASE).param("page", page).param("size", size)).andExpect(status().isOk());
    verify(service).getAlarms(null, null, expectedPage, expectedSize);
  }

  @Test
  @DisplayName("지원하지 않는 알림 상태는 400을 반환한다")
  void rejectsInvalidStatus() throws Exception {
    mvc.perform(get(BASE).param("status", "INVALID")).andExpect(status().isBadRequest());
    verifyNoInteractions(service);
  }

  @Test
  @DisplayName("알림 ID로 상세 정보와 발송 시간을 조회한다")
  void getsAlarm() throws Exception {
    LocalDateTime time = LocalDateTime.of(2026, 11, 15, 14, 0);
    when(service.getAlarm(1L))
        .thenReturn(
            new Alarm(
                1L,
                AlarmStatus.COMPLETED,
                AlarmType.INSTANT,
                AlarmTarget.all(39),
                AlarmContent.withoutLink("공지", "내용", AlarmCategory.NOTICE),
                time,
                time,
                time));
    mvc.perform(get(BASE + "/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.title").value("공지"))
        .andExpect(jsonPath("$.data.targetGeneration").value(39))
        .andExpect(jsonPath("$.data.sendDate").value("2026-11-15"))
        .andExpect(jsonPath("$.data.sendTime").value("14:00"));
    verify(service).getAlarm(1L);
  }

  @Test
  @DisplayName("알림 ID로 알림을 삭제한다")
  void deletesAlarm() throws Exception {
    mvc.perform(delete(BASE + "/1")).andExpect(status().isNoContent());
    verify(service).deleteAlarm(1L);
  }

  @Test
  @DisplayName("발송 결과의 시간을 전달해 알림 상태를 갱신한다")
  void updatesAlarmStatus() throws Exception {
    mvc.perform(
            patch(BASE + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sendAt\":\"2026-11-15 14:00\"}"))
        .andExpect(status().isNoContent());
    verify(service).updateAlarmStatus(1L, "2026-11-15 14:00");
  }

  @Test
  @DisplayName("즉시 발송에 제목이 없으면 400을 반환한다")
  void requiresTitle() throws Exception {
    mvc.perform(
            post(BASE + "/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{" + CONTENT.replace("\"title\":\"공지\",", "") + "}"))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(service);
  }
}
