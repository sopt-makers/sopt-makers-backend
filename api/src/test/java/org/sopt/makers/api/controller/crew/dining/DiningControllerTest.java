package org.sopt.makers.api.controller.crew.dining;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.sopt.makers.api.controller.crew.CrewChannelMockMvc;
import org.sopt.makers.domain.crew.dining.query.*;
import org.sopt.makers.domain.crew.dining.result.*;
import org.sopt.makers.domain.crew.dining.service.DiningV2Service;
import org.sopt.makers.domain.crew.meeting.MeetingJoinablePart;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class DiningControllerTest {
  final DiningV2Service service = mock(DiningV2Service.class);
  final MockMvc mvc = CrewChannelMockMvc.of(new DiningV2Controller(service), 10L);

  @Test
  void 목록은_기존_원시_JSON_계약과_기본페이징을_유지한다() throws Exception {
    when(service.getDinings(any(), eq(10L)))
        .thenReturn(new GetDiningsResult(List.of(), new PageMetaDto(1, 12, 0, 0)));
    mvc.perform(get("/dining/v2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.dinings").isArray())
        .andExpect(jsonPath("$.meta.take").value(12))
        .andExpect(jsonPath("$.meta.hasNextPage").value(false));
  }

  @Test
  void 참석자_조회개수와_정렬을_검증한다() throws Exception {
    mvc.perform(get("/dining/v2/1/rounds/2/attendees").param("take", "20"))
        .andExpect(status().isBadRequest());
    mvc.perform(get("/dining/v2/1/rounds/2/attendees").param("date", "bad"))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(service);
  }

  @Test
  void 회식_생성은_201과_통합사용자_ID를_전달한다() throws Exception {
    when(service.createDining(any(), eq(10L))).thenReturn(new CreateDiningResult(100L));
    mvc.perform(
            post("/dining/v2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
   {"title":"회식","diningDate":"2026.11.14","joinableParts":["SERVER"]}
   """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.meetingId").value(100));
  }

  @Test
  void 요청_날짜_형식과_파트_누락을_검증한다() throws Exception {
    mvc.perform(
            post("/dining/v2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
   {"title":"회식","diningDate":"2026-11-14","joinableParts":[]}
   """))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(service);
  }

  @Test
  void 참석취소는_204로_응답한다() throws Exception {
    mvc.perform(delete("/dining/v2/1/rounds/2/attendance")).andExpect(status().isNoContent());
    verify(service).cancelAttendance(1L, 2L, 10L);
  }

  @Test
  void 상세시간은_HH_mm이고_권한플래그는_이름을_유지한다() throws Exception {
    var round =
        new GetDiningDetailResult.Round(
            2L,
            1,
            null,
            null,
            null,
            LocalTime.of(18, 30),
            false,
            false,
            false,
            false,
            false,
            1,
            GetDiningDetailResult.MyAttendanceStatus.ATTENDING,
            null,
            false);
    when(service.getDiningDetail(1L, 10L))
        .thenReturn(
            new GetDiningDetailResult(
                1L,
                "회식",
                LocalDate.of(2026, 11, 14),
                false,
                "image",
                false,
                new MeetingJoinablePart[] {MeetingJoinablePart.SERVER},
                false,
                new DiningLeader(10L, "개설자", null),
                true,
                false,
                List.of(round)));
    mvc.perform(get("/dining/v2/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.rounds[0].startTime").value("18:30"))
        .andExpect(jsonPath("$.isLeader").value(true))
        .andExpect(jsonPath("$.isEnded").value(false));
  }
}
