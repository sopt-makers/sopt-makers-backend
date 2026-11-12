package org.sopt.makers.api.controller.crew.meeting;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sopt.makers.api.controller.crew.CrewChannelMockMvc;
import org.sopt.makers.domain.crew.meeting.Meeting;
import org.sopt.makers.domain.crew.meeting.MeetingCategory;
import org.sopt.makers.domain.crew.meeting.MeetingStatus;
import org.sopt.makers.domain.crew.meeting.MeetingUser;
import org.sopt.makers.domain.crew.meeting.facade.MeetingFacade;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;
import org.sopt.makers.domain.crew.meeting.tag.service.MeetingTagService;
import org.springframework.test.web.servlet.MockMvc;

class MeetingResponseContractTest {

  private static final Long USER_ID = 10L;
  private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 8, 1, 12, 0);
  private final MeetingService service = mock(MeetingService.class);
  private final MeetingFacade facade = mock(MeetingFacade.class);
  private final MockMvc mockMvc =
      CrewChannelMockMvc.of(new MeetingController(service, facade), USER_ID);

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void 상세는_기존_필드명으로_모임장_여부와_사용자_신청목록을_반환한다(boolean host) throws Exception {
    when(facade.getMeetingDetail(1L, USER_ID)).thenReturn(detail(host, List.of()));

    mockMvc
        .perform(get("/meeting/v2/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.host").value(host))
        .andExpect(jsonPath("$.data.status").value(1))
        .andExpect(jsonPath("$.data.createdTimestamp").value("2026-08-01T12:00:00"))
        .andExpect(jsonPath("$.data.meetingDemandId").value(7))
        .andExpect(jsonPath("$.data.user.name").value("모임장"))
        .andExpect(jsonPath("$.data.appliedInfo").isEmpty())
        .andExpect(jsonPath("$.data.isHost").doesNotExist())
        .andExpect(jsonPath("$.data.meetingCreator").doesNotExist())
        .andExpect(jsonPath("$.data.applies").doesNotExist());
  }

  private MeetingFacade.MeetingDetailResult detail(
      boolean host, List<MeetingService.ApplyDetail> applies) {
    return new MeetingFacade.MeetingDetailResult(
        new MeetingService.MeetingDetail(
            meeting(),
            new MeetingUser(USER_ID, "모임장", "leader.png", List.of()),
            List.of(),
            host,
            false,
            false,
            false,
            0,
            applies,
            MeetingStatus.APPLY_ABLE),
        MeetingTagService.MeetingTagInfo.empty());
  }

  private Meeting meeting() {
    return new Meeting(
        1L,
        7L,
        "스터디",
        "함께 공부",
        MeetingCategory.STUDY,
        List.of(),
        LocalDateTime.of(2026, 9, 1, 0, 0),
        LocalDateTime.of(2026, 9, 2, 0, 0),
        10,
        "소개",
        null,
        null,
        null,
        null,
        null,
        false,
        false,
        null,
        36,
        36,
        List.of(),
        CREATED_AT,
        CREATED_AT);
  }
}
