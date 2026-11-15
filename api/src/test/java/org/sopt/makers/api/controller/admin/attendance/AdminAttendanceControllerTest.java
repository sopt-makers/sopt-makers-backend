package org.sopt.makers.api.controller.admin.attendance;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
import org.sopt.makers.domain.admin.member.Member;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AdminAttendanceControllerTest {
  private final AttendanceService service = mock(AttendanceService.class);
  private final MockMvc mvc =
      MockMvcBuilders.standaloneSetup(new AdminAttendanceController(service))
          .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
          .setControllerAdvice(new GlobalExceptionHandler())
          .build();

  @Test
  @DisplayName("회원의 출석 내역과 시간 및 표시명을 반환한다")
  void returnsAttendanceDetails() throws Exception {
    LocalDateTime start = LocalDateTime.of(2026, 10, 1, 14, 0);
    MemberAttendanceDetail detail =
        new MemberAttendanceDetail(
            "세미나",
            start,
            start.plusHours(4),
            -0.5f,
            AttendanceStatus.TARDY,
            List.of(
                new MemberAttendanceDetail.SubAttendanceDetail(
                    1, AttendanceStatus.ATTENDANCE, start.plusMinutes(10))));
    when(service.getAttendancesByUserId(10L))
        .thenReturn(
            new UserAttendanceSummary(
                new Member(10L, "홍길동", 38, Part.SERVER, 1.5f, 0, 0, 0, 0), List.of(detail)));

    mvc.perform(get("/api/v1/attendances/10"))
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
    verify(service).getAttendancesByUserId(10L);
  }

  @Test
  @DisplayName("출석 내역이 없으면 사용자 정보와 빈 세션 목록을 반환한다")
  void returnsEmptyLectures() throws Exception {
    when(service.getAttendancesByUserId(10L))
        .thenReturn(
            new UserAttendanceSummary(
                new Member(10L, "홍길동", 38, Part.SERVER, 2.0f, 0, 0, 0, 0), List.of()));
    mvc.perform(get("/api/v1/attendances/10"))
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
    when(service.getAttendancesByUserId(10L))
        .thenThrow(new AttendanceException(AttendanceFailure.NOT_FOUND_ATTENDANCE));
    mvc.perform(get("/api/v1/attendances/10"))
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
  @DisplayName("회원 ID만으로 출석 점수를 갱신하고 숫자로 반환한다")
  void updatesScore() throws Exception {
    when(service.updateAttendanceScore(10L)).thenReturn(1.5f);
    mvc.perform(patch("/api/v1/attendances/member/10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").value(1.5));
    verify(service).updateAttendanceScore(10L);
  }

  @Test
  @DisplayName("세부 출석을 수정하고 ID와 상태를 반환한다")
  void updatesSubAttendance() throws Exception {
    when(service.updateSubAttendance(1L, AttendanceStatus.TARDY))
        .thenReturn(new SubAttendance(1L, 2L, 3L, 1, null, AttendanceStatus.TARDY, null));
    mvc.perform(
            patch("/api/v1/attendances")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"subAttendanceId\":1,\"status\":\"TARDY\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.subAttendanceId").value(1))
        .andExpect(jsonPath("$.data.status").value("TARDY"));
  }

  @Test
  @DisplayName("세션 출석에서 회원 정보와 수정 시각 및 가산 점수를 반환한다")
  void getsLectureAttendances() throws Exception {
    LocalDateTime time = LocalDateTime.of(2026, 11, 15, 14, 0);
    when(service.countAttendancesByLecture(1L, Part.SERVER)).thenReturn(1);
    when(service.getAttendancesByLectureId(1L, Part.SERVER, 1, 5))
        .thenReturn(
            List.of(
                new LectureAttendanceDetail(
                    2L,
                    10L,
                    "홍길동",
                    -0.5f,
                    List.of(
                        new LectureAttendanceDetail.SubAttendanceDetail(
                            3L, 1, AttendanceStatus.TARDY, time)))));
    mvc.perform(
            get("/api/v1/attendances/lecture/1")
                .param("part", "SERVER")
                .param("page", "1")
                .param("size", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.totalCount").value(1))
        .andExpect(jsonPath("$.data.attendances[0].member.memberId").value(10))
        .andExpect(jsonPath("$.data.attendances[0].member.name").value("홍길동"))
        .andExpect(jsonPath("$.data.attendances[0].updatedScore").value(-0.5))
        .andExpect(
            jsonPath("$.data.attendances[0].attendances[0].updateAt").value("2026-11-15T14:00"));
  }
}
