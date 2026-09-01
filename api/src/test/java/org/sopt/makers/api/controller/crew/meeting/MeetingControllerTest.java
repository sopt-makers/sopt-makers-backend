package org.sopt.makers.api.controller.crew.meeting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sopt.makers.api.controller.app.AppChannelMockMvc;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.crew.meeting.MeetingCategory;
import org.sopt.makers.domain.crew.meeting.MeetingStatus;
import org.sopt.makers.domain.crew.meeting.facade.MeetingFacade;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;
import org.springframework.test.web.servlet.MockMvc;

class MeetingControllerTest {

  private static final Long USER_ID = 10L;

  private final MeetingService meetingService = mock(MeetingService.class);
  private final MeetingFacade meetingFacade = mock(MeetingFacade.class);
  private final MockMvc mockMvc =
      AppChannelMockMvc.of(new MeetingController(meetingService, meetingFacade), USER_ID);

  @Test
  void 모임_목록은_검색어_카테고리_상태를_전달한다() throws Exception {
    when(meetingFacade.searchMeetings(any(), any(Integer.class), any(Integer.class)))
        .thenReturn(emptyPage());

    mockMvc
        .perform(
            get("/meeting/v2")
                .param("search", "러닝")
                .param("category", "스터디")
                .param("status", "1")
                .param("pageNo", "1")
                .param("limit", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.totalCount").value(0));

    ArgumentCaptor<MeetingService.SearchMeetingsCommand> commandCaptor =
        ArgumentCaptor.forClass(MeetingService.SearchMeetingsCommand.class);
    verify(meetingFacade)
        .searchMeetings(commandCaptor.capture(), any(Integer.class), any(Integer.class));
    assertThat(commandCaptor.getValue().search()).isEqualTo("러닝");
    assertThat(commandCaptor.getValue().category()).isEqualTo(MeetingCategory.STUDY);
    assertThat(commandCaptor.getValue().status()).isEqualTo(MeetingStatus.APPLY_ABLE);
  }

  @Test
  void 공동_모임장은_지원자_목록_API를_호출할_수_있다() throws Exception {
    when(meetingService.getApplicants(1L, USER_ID)).thenReturn(List.of());

    mockMvc
        .perform(get("/meeting/v2/1/applicants"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.applicants").isArray());

    verify(meetingService).getApplicants(1L, USER_ID);
  }

  @Test
  void 모임_구성원은_참여자_목록_API를_호출할_수_있다() throws Exception {
    when(meetingService.getParticipants(1L, USER_ID)).thenReturn(List.of());

    mockMvc
        .perform(get("/meeting/v2/1/participants"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.participants").isArray());

    verify(meetingService).getParticipants(1L, USER_ID);
  }

  @Test
  void 내가_속한_모임_API는_역할_기반_목록을_조회한다() throws Exception {
    when(meetingFacade.findJoinedMeetings(USER_ID, 1, 10)).thenReturn(emptyJoinedPage());

    mockMvc
        .perform(get("/meeting/v2/me/joined"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.data").isArray());

    verify(meetingFacade).findJoinedMeetings(USER_ID, 1, 10);
  }

  private PageResult<MeetingFacade.MeetingSummaryResult> emptyPage() {
    return new PageResult<>(List.of(), 0, 0, 1, 20, false, false);
  }

  private PageResult<MeetingFacade.JoinedMeetingResult> emptyJoinedPage() {
    return new PageResult<>(List.of(), 0, 0, 1, 10, false, false);
  }
}
