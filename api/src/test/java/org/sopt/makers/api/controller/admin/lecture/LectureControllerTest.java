package org.sopt.makers.api.controller.admin.lecture;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sopt.makers.api.common.exception.GlobalExceptionHandler;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.lecture.AttendanceStatusSummary;
import org.sopt.makers.domain.admin.lecture.Lecture;
import org.sopt.makers.domain.admin.lecture.LectureAttribute;
import org.sopt.makers.domain.admin.lecture.LectureStatus;
import org.sopt.makers.domain.admin.lecture.SubLecture;
import org.sopt.makers.domain.admin.lecture.service.LectureService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class LectureControllerTest {
  private static final String BASE = "/api/v1/lectures";
  private static final LocalDateTime START = LocalDateTime.of(2026, 11, 15, 14, 0);
  private static final Lecture LECTURE =
      new Lecture(
          1L,
          "세미나",
          Part.SERVER,
          39,
          "강의실",
          START,
          START.plusHours(4),
          LectureAttribute.SEMINAR,
          LectureStatus.BEFORE,
          List.of());
  private static final AttendanceStatusSummary SUMMARY = new AttendanceStatusSummary(1, 2, 3, 0);

  private final LectureService service = mock(LectureService.class);
  private final MockMvc mvc =
      MockMvcBuilders.standaloneSetup(new LectureController(service))
          .setControllerAdvice(new GlobalExceptionHandler())
          .build();

  @ParameterizedTest
  @ValueSource(
      strings = {"2026-11-15T14:00", "2026-11-15T14:00:00", "2026-11-15T14:00:00.123456789"})
  @DisplayName("ISO 날짜와 시간으로 세션을 생성하고 201을 반환한다")
  void createsLecture(String startDate) throws Exception {
    LocalDateTime start = LocalDateTime.parse(startDate);
    LocalDateTime end = start.plusHours(4);
    when(service.createLecture(Part.SERVER, "세미나", 39, "강의실", start, end, LectureAttribute.SEMINAR))
        .thenReturn(LECTURE);

    mvc.perform(
            post(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
        {"part":"SERVER","name":"세미나","generation":39,"place":"강의실",
         "startDate":"%s","endDate":"%s","attribute":"SEMINAR"}
        """
                        .formatted(startDate, end)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data").value(1));
    verify(service)
        .createLecture(Part.SERVER, "세미나", 39, "강의실", start, end, LectureAttribute.SEMINAR);
  }

  @Test
  @DisplayName("기수와 파트 조건으로 세션 목록과 출석 집계를 조회한다")
  void getsLectures() throws Exception {
    when(service.getLectures(39, Part.SERVER)).thenReturn(List.of(LECTURE));
    when(service.getAttendanceSummaries(List.of(LECTURE))).thenReturn(Map.of(1L, SUMMARY));

    mvc.perform(get(BASE).param("generation", "39").param("part", "SERVER"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.generation").value(39))
        .andExpect(jsonPath("$.data.lectures[0].lectureId").value(1))
        .andExpect(jsonPath("$.data.lectures[0].attendances.attendance").value(1));
    verify(service).getLectures(39, Part.SERVER);
  }

  @Test
  @DisplayName("파트를 지정하지 않으면 기수 전체의 세션을 조회한다")
  void getsLecturesWithoutPart() throws Exception {
    when(service.getLectures(39, null)).thenReturn(List.of());
    when(service.getAttendanceSummaries(List.of())).thenReturn(Map.of());

    mvc.perform(get(BASE).param("generation", "39"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.lectures").isEmpty());
    verify(service).getLectures(39, null);
  }

  @Test
  @DisplayName("목록 조회에 기수가 없으면 400을 반환한다")
  void requiresGeneration() throws Exception {
    mvc.perform(get(BASE)).andExpect(status().isBadRequest());
    verifyNoInteractions(service);
  }

  @Test
  @DisplayName("세션 ID로 세션 정보와 출석 집계를 조회한다")
  void getsLecture() throws Exception {
    when(service.getLecture(1L)).thenReturn(LECTURE);
    when(service.getAttendanceSummary(LECTURE)).thenReturn(SUMMARY);

    mvc.perform(get(BASE + "/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.lectureId").value(1))
        .andExpect(jsonPath("$.data.attendances.absent").value(2));
    verify(service).getLecture(1L);
  }

  @Test
  @DisplayName("세션 ID와 회차 및 코드를 전달하면 출석을 시작한다")
  void startsAttendance() throws Exception {
    when(service.startSubLecture(1L, 1, "1234"))
        .thenReturn(new SubLecture(10L, 1L, LectureAttribute.SEMINAR, 39, 1, START, "1234"));

    mvc.perform(
            patch(BASE + "/attendance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
            {"lectureId":1,"round":1,"code":"1234"}
            """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.lectureId").value(1))
        .andExpect(jsonPath("$.data.subLectureId").value(10));
    verify(service).startSubLecture(1L, 1, "1234");
  }

  @Test
  @DisplayName("출석 코드가 비어 있으면 400을 반환한다")
  void rejectsBlankAttendanceCode() throws Exception {
    mvc.perform(
            patch(BASE + "/attendance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
            {"lectureId":1,"round":1,"code":""}
            """))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(service);
  }

  @Test
  @DisplayName("세션 ID로 세션을 종료한다")
  void endsLecture() throws Exception {
    mvc.perform(patch(BASE + "/1")).andExpect(status().isOk());
    verify(service).endLecture(1L);
  }

  @Test
  @DisplayName("세션 ID로 세션을 삭제한다")
  void deletesLecture() throws Exception {
    mvc.perform(delete(BASE + "/1")).andExpect(status().isOk());
    verify(service).deleteLecture(1L);
  }

  @Test
  @DisplayName("세션 ID로 팝업에 표시할 상세 정보를 조회한다")
  void getsLectureDetail() throws Exception {
    when(service.getLectureDetail(1L)).thenReturn(LECTURE);
    mvc.perform(get(BASE + "/detail/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.lectureId").value(1))
        .andExpect(jsonPath("$.data.part").value("서버"))
        .andExpect(jsonPath("$.data.name").value("세미나"));
    verify(service).getLectureDetail(1L);
  }
}
