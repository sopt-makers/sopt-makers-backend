package org.sopt.makers.api.controller.app;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.*;
import org.sopt.makers.api.common.exception.GlobalExceptionHandler;
import org.sopt.makers.api.common.resolver.CurrentUserIdArgumentResolver;
import org.sopt.makers.api.common.security.authentication.CustomAuthentication;
import org.sopt.makers.api.controller.app.attendance.AppAttendanceController;
import org.sopt.makers.api.controller.app.lecture.AppLectureController;
import org.sopt.makers.api.controller.app.member.AppMemberController;
import org.sopt.makers.api.controller.app.schedule.AppScheduleController;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.app.*;
import org.sopt.makers.domain.admin.app.service.*;
import org.sopt.makers.domain.admin.attendance.AttendanceStatus;
import org.sopt.makers.domain.admin.attendance.SubAttendance;
import org.sopt.makers.domain.admin.attendance.exception.*;
import org.sopt.makers.domain.admin.attendance.service.AttendanceService;
import org.sopt.makers.domain.admin.lecture.*;
import org.sopt.makers.domain.admin.member.Member;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AppOperationControllerTest {
  private final AppLectureService lectures = mock(AppLectureService.class);
  private final AppMemberService members = mock(AppMemberService.class);
  private final AttendanceService attendances = mock(AttendanceService.class);
  private final AppScheduleService schedules = mock(AppScheduleService.class);
  private final MockMvc mvc =
      MockMvcBuilders.standaloneSetup(
              new AppLectureController(lectures),
              new AppMemberController(members),
              new AppAttendanceController(attendances),
              new AppScheduleController(schedules))
          .setCustomArgumentResolvers(new CurrentUserIdArgumentResolver())
          .setControllerAdvice(new GlobalExceptionHandler())
          .build();
  private static final String BASE = "/api/v1/app";
  private static final LocalDateTime TIME = LocalDateTime.of(2026, 11, 15, 14, 0);

  @BeforeEach
  void authenticate() {
    SecurityContextHolder.getContext()
        .setAuthentication(new CustomAuthentication("10", "", List.of()));
  }

  @AfterEach
  void clearAuthentication() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("출석 코드를 전달하면 인증 회원으로 처리하고 세부 세션 ID를 반환한다")
  void attends() throws Exception {
    when(attendances.attend(10L, 2L, "1234")).thenReturn(2L);
    mvc.perform(
            post(BASE + "/attendances/attend")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"subLectureId\":2,\"code\":\"1234\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.subLectureId").value(2));
    verify(attendances).attend(10L, 2L, "1234");
  }

  @Test
  @DisplayName("오늘 세션은 실제 출석 시각을 초 단위로 반환한다")
  void getsTodayLecture() throws Exception {
    var sub =
        new SubAttendance(1L, 1L, 1L, 1, TIME, AttendanceStatus.ATTENDANCE, TIME.plusMinutes(3));
    var lecture =
        new AppLecture(
            1L,
            "강의실",
            "세미나",
            TIME,
            TIME.plusHours(4),
            LectureAttribute.SEMINAR,
            LectureStatus.FIRST,
            List.of(sub));
    when(lectures.getTodayLecture(10L))
        .thenReturn(
            new AppLectureResult(AppLectureResponseType.HAS_ATTENDANCE, lecture, "", List.of(sub)));
    mvc.perform(get(BASE + "/lectures"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.message").value(""))
        .andExpect(jsonPath("$.data.startDate").value("2026-11-15T14:00:00"))
        .andExpect(jsonPath("$.data.attendances[0].attendedAt").value("2026-11-15T14:03:00"));
    verify(lectures).getTodayLecture(10L);
  }

  @Test
  @DisplayName("오늘 세션이 없으면 빈 문자열과 빈 출석 목록을 반환한다")
  void getsEmptyLecture() throws Exception {
    when(lectures.getTodayLecture(10L)).thenReturn(AppLectureResult.empty());
    mvc.perform(get(BASE + "/lectures"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.type").value("NO_SESSION"))
        .andExpect(jsonPath("$.data.id").value(0))
        .andExpect(jsonPath("$.data.name").value(""))
        .andExpect(jsonPath("$.data.attendances").isEmpty());
  }

  @Test
  @DisplayName("현재 출석 차수의 ID와 회차를 반환한다")
  void getsRound() throws Exception {
    when(lectures.getCurrentLectureRound(1L)).thenReturn(new AppSubLecture(2L, 1, TIME));
    mvc.perform(get(BASE + "/lectures/round/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(2))
        .andExpect(jsonPath("$.data.round").value(1));
  }

  @Test
  @DisplayName("종료된 출석이 없으면 기수와 0으로 집계된 출석 횟수를 반환한다")
  void getsMemberAttendances() throws Exception {
    when(members.getMemberTotalAttendance(10L))
        .thenReturn(
            new AppMemberAttendanceSummary(
                new Member(10L, "홍길동", 39, Part.SERVER, 2f, 3, 2, 1, 1), List.of()));
    mvc.perform(get(BASE + "/members/attendances"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.generation").value(39))
        .andExpect(jsonPath("$.data.part").value("SERVER"))
        .andExpect(jsonPath("$.data.score").value(2))
        .andExpect(jsonPath("$.data.total.attendance").value(0))
        .andExpect(jsonPath("$.data.total.absent").value(0))
        .andExpect(jsonPath("$.data.attendances").isEmpty());
    verify(members).getMemberTotalAttendance(10L);
  }

  @Test
  @DisplayName("인증 회원의 출석 점수를 객체로 반환한다")
  void getsScore() throws Exception {
    when(members.getMemberScore(10L)).thenReturn(1.5f);
    mvc.perform(get(BASE + "/members/score"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.score").value(1.5));
    verify(members).getMemberScore(10L);
  }

  @Test
  @DisplayName("ISO 날짜와 시간의 조회 조건을 전달하고 날짜순 일정을 반환한다")
  void getsSchedules() throws Exception {
    var schedule = new AppSchedule(1L, TIME, TIME.plusHours(2), LectureAttribute.EVENT, "행사");
    when(schedules.getSchedules(TIME, TIME.plusDays(1)))
        .thenReturn(
            Map.of(
                TIME.plusDays(1).toLocalDate(), List.of(), TIME.toLocalDate(), List.of(schedule)));
    mvc.perform(
            get(BASE + "/schedules")
                .param("start", TIME.toString())
                .param("end", TIME.plusDays(1).toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.dates[0].date").value("2026-11-15"))
        .andExpect(jsonPath("$.data.dates[0].schedules[0].scheduleId").value(1))
        .andExpect(jsonPath("$.data.dates[1].schedules").isEmpty());
    verify(schedules).getSchedules(TIME, TIME.plusDays(1));
  }

  @Test
  @DisplayName("운영 기수 회원이 아니면 404를 반환한다")
  void rejectsInactiveMember() throws Exception {
    when(members.getMemberScore(10L))
        .thenThrow(new AttendanceException(AttendanceFailure.NOT_FOUND_ATTENDANCE));
    mvc.perform(get(BASE + "/members/score")).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("일정 조회 종료 시각이 없으면 400을 반환한다")
  void requiresEnd() throws Exception {
    mvc.perform(get(BASE + "/schedules").param("start", TIME.toString()))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(schedules);
  }
}
