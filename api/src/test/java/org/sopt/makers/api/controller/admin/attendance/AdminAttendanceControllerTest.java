package org.sopt.makers.api.controller.admin.attendance;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.api.common.exception.GlobalExceptionHandler;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.attendance.*;
import org.sopt.makers.domain.admin.attendance.exception.*;
import org.sopt.makers.domain.admin.attendance.service.AttendanceService;
import org.sopt.makers.domain.admin.user.UserActivity;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AdminAttendanceControllerTest {
  private final AttendanceService service = mock(AttendanceService.class);
  private final MockMvc mvc =
      MockMvcBuilders.standaloneSetup(new AdminAttendanceController(service))
          .setControllerAdvice(new GlobalExceptionHandler())
          .build();

  @Test
  @DisplayName("유저별 출석 조회는 레거시 필드 구조와 시간 및 표시명을 반환한다")
  void returnsLegacyResponseContract() throws Exception {
    LocalDateTime start = LocalDateTime.of(2026, 10, 1, 14, 0);
    AttendanceDetail detail =
        new AttendanceDetail(
            "세미나",
            start,
            start.plusHours(4),
            -0.5f,
            AttendanceStatus.TARDY,
            List.of(
                new AttendanceDetail.SubAttendanceDetail(
                    1, AttendanceStatus.ATTENDANCE, start.plusMinutes(10))));
    when(service.getAttendancesByUserId(10L, 38))
        .thenReturn(
            new UserAttendanceSummary(
                new UserActivity(10L, "홍길동", 38, Part.SERVER, 1.5f), List.of(detail)));

    mvc.perform(get("/api/v1/admin/attendances/users/10").param("generation", "38"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
          {"success":true,"message":"유저 출석 목록 조회가 완료되었습니다","data":{
            "name":"홍길동","score":1.5,"part":"서버","lectures":[{
              "startDate":"2026-10-01T14:00:00","endDate":"2026-10-01T18:00:00",
              "lecture":"세미나","additiveScore":-0.5,"status":"지각",
              "attendances":[{"round":1,"status":"출석","date":"2026-10-01T14:10"}]
            }]}}
          """,
                    JsonCompareMode.STRICT));
    verify(service).getAttendancesByUserId(10L, 38);
  }

  @Test
  @DisplayName("출석 내역이 없으면 사용자 정보와 빈 세션 목록을 반환한다")
  void returnsEmptyLectures() throws Exception {
    when(service.getAttendancesByUserId(10L, 38))
        .thenReturn(
            new UserAttendanceSummary(
                new UserActivity(10L, "홍길동", 38, Part.SERVER, 2.0f), List.of()));
    mvc.perform(get("/api/v1/admin/attendances/users/10").param("generation", "38"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
          {"success":true,"message":"유저 출석 목록 조회가 완료되었습니다",
           "data":{"name":"홍길동","score":2.0,"part":"서버","lectures":[]}}
          """,
                    JsonCompareMode.STRICT));
  }

  @Test
  @DisplayName("출석 조회에 실패하면 404와 공통 실패 응답을 반환한다")
  void returnsNotFound() throws Exception {
    when(service.getAttendancesByUserId(10L, 38))
        .thenThrow(new AttendanceException(AttendanceFailure.NOT_FOUND_ATTENDANCE));
    mvc.perform(get("/api/v1/admin/attendances/users/10").param("generation", "38"))
        .andExpect(status().isNotFound())
        .andExpect(
            content()
                .json(
                    """
          {"success":false,"message":"출석 정보를 찾을 수 없습니다","data":null}
          """,
                    JsonCompareMode.STRICT));
  }

  @Test
  @DisplayName("기수를 지정하지 않으면 400을 반환하고 서비스를 호출하지 않는다")
  void requiresGeneration() throws Exception {
    mvc.perform(get("/api/v1/admin/attendances/users/10")).andExpect(status().isBadRequest());
    verifyNoInteractions(service);
  }
}
