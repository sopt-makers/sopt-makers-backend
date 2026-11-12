package org.sopt.makers.api.controller.crew.meeting;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.sopt.makers.api.controller.crew.CrewChannelMockMvc;
import org.sopt.makers.api.controller.crew.flash.FlashController;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.crew.flash.Flash;
import org.sopt.makers.domain.crew.flash.FlashPlaceType;
import org.sopt.makers.domain.crew.flash.FlashTimingType;
import org.sopt.makers.domain.crew.flash.facade.FlashFacade;
import org.sopt.makers.domain.crew.meeting.Meeting;
import org.sopt.makers.domain.crew.meeting.MeetingApply;
import org.sopt.makers.domain.crew.meeting.MeetingApplyStatus;
import org.sopt.makers.domain.crew.meeting.MeetingApplyType;
import org.sopt.makers.domain.crew.meeting.MeetingCategory;
import org.sopt.makers.domain.crew.meeting.MeetingStatus;
import org.sopt.makers.domain.crew.meeting.MeetingUser;
import org.sopt.makers.domain.crew.meeting.Member;
import org.sopt.makers.domain.crew.meeting.facade.MeetingFacade;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;
import org.sopt.makers.domain.crew.meeting.tag.service.MeetingTagService;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.Team;
import org.springframework.http.MediaType;
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

  @Test
  void 모임장과_공동모임장과_신청자는_기존_사용자_필드를_반환한다() throws Exception {
    MeetingService.ApplyDetail applicant =
        new MeetingService.ApplyDetail(
            MeetingApply.createApply(1L, 20L, "신청합니다", CREATED_AT), profile(20L));
    when(facade.getMeetingDetail(1L, USER_ID)).thenReturn(detail(true, List.of(applicant)));

    mockMvc
        .perform(get("/meeting/v2/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.user.id").value(10))
        .andExpect(jsonPath("$.data.user.orgId").value(10))
        .andExpect(jsonPath("$.data.user.profileImage").value("leader.png"))
        .andExpect(jsonPath("$.data.user.phone").value("01012345678"))
        .andExpect(jsonPath("$.data.user.activities.length()").value(2))
        .andExpect(jsonPath("$.data.user.activities[0].part").value("웹"))
        .andExpect(jsonPath("$.data.user.activities[0].generation").value(35))
        .andExpect(jsonPath("$.data.user.activities[1].part").value("서버"))
        .andExpect(jsonPath("$.data.user.activities[1].generation").value(36))
        .andExpect(jsonPath("$.data.user.userId").doesNotExist())
        .andExpect(jsonPath("$.data.user.generation").doesNotExist())
        .andExpect(jsonPath("$.data.coMeetingLeaders[0].userId").value(30))
        .andExpect(jsonPath("$.data.coMeetingLeaders[0].orgId").value(30))
        .andExpect(jsonPath("$.data.coMeetingLeaders[0].userName").value("공동 모임장"))
        .andExpect(jsonPath("$.data.coMeetingLeaders[0].userprofileImage").value("co-leader.png"))
        .andExpect(jsonPath("$.data.coMeetingLeaders[0].name").doesNotExist())
        .andExpect(jsonPath("$.data.coMeetingLeaders[0].profileImage").doesNotExist())
        .andExpect(jsonPath("$.data.appliedInfo[0].user.id").value(20))
        .andExpect(jsonPath("$.data.appliedInfo[0].user.orgId").value(20))
        .andExpect(jsonPath("$.data.appliedInfo[0].user.activities[0].part").value("웹"));
  }

  @ParameterizedTest
  @CsvSource({
    "1, 10, 0, 0, false, false",
    "1, 10, 1, 1, false, false",
    "1, 10, 21, 3, false, true",
    "2, 10, 21, 3, true, true",
    "3, 10, 21, 3, true, false"
  })
  void 모임_목록은_meetings와_기존_meta를_반환한다(
      int page, int take, long count, int pages, boolean previous, boolean next) throws Exception {
    MeetingService.MeetingSummary summary =
        new MeetingService.MeetingSummary(
            meeting(), Member.leader(1L, USER_ID), 0, 0, MeetingStatus.APPLY_ABLE);
    MeetingFacade.MeetingSummaryResult result =
        new MeetingFacade.MeetingSummaryResult(summary, MeetingTagService.MeetingTagInfo.empty());
    PageResult<MeetingFacade.MeetingSummaryResult> meetings =
        new PageResult<>(
            count == 0 ? List.of() : List.of(result), count, pages, page, take, next, previous);
    when(facade.searchMeetings(any(), eq(page), eq(take))).thenReturn(meetings);
    when(facade.findMeetingsByCreator(USER_ID, page, take)).thenReturn(meetings);
    MeetingFacade.JoinedMeetingResult joined =
        new MeetingFacade.JoinedMeetingResult(
            new MeetingService.JoinedMeeting(summary, Member.participant(1L, USER_ID)),
            MeetingTagService.MeetingTagInfo.empty());
    when(facade.findJoinedMeetings(USER_ID, page, take))
        .thenReturn(
            new PageResult<>(
                count == 0 ? List.of() : List.of(joined),
                count,
                pages,
                page,
                take,
                next,
                previous));

    for (String path : List.of("/meeting/v2", "/meeting/v2/me", "/meeting/v2/me/joined")) {
      mockMvc
          .perform(
              get(path)
                  .param("pageNo", Integer.toString(page))
                  .param("limit", Integer.toString(take)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.meetings.length()").value(count == 0 ? 0 : 1))
          .andExpect(jsonPath("$.data.meta.page").value(page))
          .andExpect(jsonPath("$.data.meta.take").value(take))
          .andExpect(jsonPath("$.data.meta.itemCount").value(count))
          .andExpect(jsonPath("$.data.meta.pageCount").value(pages))
          .andExpect(jsonPath("$.data.meta.hasPreviousPage").value(previous))
          .andExpect(jsonPath("$.data.meta.hasNextPage").value(next))
          .andExpect(jsonPath("$.data.data").doesNotExist())
          .andExpect(jsonPath("$.data.totalCount").doesNotExist())
          .andExpect(jsonPath("$.data.meta.totalCount").doesNotExist());
    }
  }

  @ParameterizedTest
  @CsvSource({
    "/meeting/v2/1, appliedInfo",
    "/meeting/v2/1/applicants, applicants",
    "/meeting/v2/1/members, appliedInfo"
  })
  void 신청_번호는_응답_목록마다_1부터_연속된_숫자다(String path, String listKey) throws Exception {
    List<MeetingService.ApplyDetail> applies =
        List.of(application(41L, 20L), application(97L, 30L));
    when(facade.getMeetingDetail(1L, USER_ID)).thenReturn(detail(true, applies));
    when(service.getApplicants(1L, USER_ID)).thenReturn(applies);
    when(service.getMeetingPartMembers(1L, USER_ID))
        .thenReturn(new MeetingService.MeetingPartMembers("서버", 2, true, 36, applies));

    for (int request = 0; request < 2; request++) {
      mockMvc
          .perform(get(path))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data." + listKey + "[0].applyNumber").value(1))
          .andExpect(jsonPath("$.data." + listKey + "[0].id").value(41))
          .andExpect(jsonPath("$.data." + listKey + "[0].user.id").value(20))
          .andExpect(jsonPath("$.data." + listKey + "[1].applyNumber").value(2))
          .andExpect(jsonPath("$.data." + listKey + "[1].id").value(97))
          .andExpect(jsonPath("$.data." + listKey + "[1].user.id").value(30));
    }
  }

  @ParameterizedTest
  @CsvSource({
    "/meeting/v2/1, appliedInfo",
    "/meeting/v2/1/applicants, applicants",
    "/meeting/v2/1/members, appliedInfo"
  })
  void 신청이_없으면_빈_배열을_반환한다(String path, String listKey) throws Exception {
    when(facade.getMeetingDetail(1L, USER_ID)).thenReturn(detail(false, List.of()));
    when(service.getApplicants(1L, USER_ID)).thenReturn(List.of());
    when(service.getMeetingPartMembers(1L, USER_ID))
        .thenReturn(new MeetingService.MeetingPartMembers("서버", 0, true, 36, List.of()));

    mockMvc
        .perform(get(path))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data." + listKey).isArray())
        .andExpect(jsonPath("$.data." + listKey).isEmpty());
  }

  @Test
  void 활동과_공동모임장이_없고_프로필이_비어도_상세_계약을_유지한다() throws Exception {
    MeetingService.MeetingDetail meetingDetail =
        new MeetingService.MeetingDetail(
            meeting(),
            new MeetingUser(USER_ID, "모임장", null, List.of()),
            List.of(),
            true,
            false,
            false,
            false,
            0,
            List.of(),
            MeetingStatus.RECRUITMENT_COMPLETE);
    when(facade.getMeetingDetail(1L, USER_ID))
        .thenReturn(
            new MeetingFacade.MeetingDetailResult(
                meetingDetail, MeetingTagService.MeetingTagInfo.empty()));

    mockMvc
        .perform(get("/meeting/v2/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value(2))
        .andExpect(jsonPath("$.data.coMeetingLeaders").isEmpty())
        .andExpect(jsonPath("$.data.user.activities").isEmpty())
        .andExpect(jsonPath("$.data.user.profileImage").value(nullValue()))
        .andExpect(jsonPath("$.data.user.phone").value(nullValue()));
  }

  @Test
  void 수정_응답도_조회와_같은_상세_계약을_반환한다() throws Exception {
    when(facade.updateMeeting(eq(1L), any(), eq(USER_ID)))
        .thenReturn(detail(true, List.of(application(41L, 20L))));

    mockMvc
        .perform(
            patch("/meeting/v2/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"스터디\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.host").value(true))
        .andExpect(jsonPath("$.data.status").value(1))
        .andExpect(jsonPath("$.data.createdTimestamp").value("2026-08-01T12:00:00"))
        .andExpect(jsonPath("$.data.user.id").value(USER_ID))
        .andExpect(jsonPath("$.data.coMeetingLeaders[0].userName").value("공동 모임장"))
        .andExpect(jsonPath("$.data.appliedInfo[0].applyNumber").value(1))
        .andExpect(jsonPath("$.data.isHost").doesNotExist());
  }

  @Test
  void 신청_DTO를_공유하는_번쩍_상세도_신청번호를_반환한다() throws Exception {
    FlashFacade flashFacade = mock(FlashFacade.class);
    MockMvc flashMvc = CrewChannelMockMvc.of(new FlashController(flashFacade), USER_ID);
    Flash flash =
        new Flash(
            2L,
            USER_ID,
            1L,
            "번쩍 모임",
            "소개",
            FlashTimingType.IMMEDIATE,
            CREATED_AT,
            CREATED_AT.plusHours(1),
            CREATED_AT.plusHours(2),
            CREATED_AT.plusHours(3),
            FlashPlaceType.OFFLINE,
            "서울",
            2,
            10,
            36,
            List.of(),
            CREATED_AT,
            CREATED_AT);
    when(flashFacade.getFlashDetail(1L, USER_ID))
        .thenReturn(
            new FlashFacade.FlashDetail(
                flash,
                detail(true, List.of(application(41L, 20L), application(97L, 30L))).meetingDetail(),
                MeetingTagService.MeetingTagInfo.empty(),
                MeetingStatus.APPLY_ABLE));

    flashMvc
        .perform(get("/flash/v2/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.appliedInfo[0].applyNumber").value(1))
        .andExpect(jsonPath("$.data.appliedInfo[1].applyNumber").value(2))
        .andExpect(jsonPath("$.data.appliedInfo[0].user.id").value(20))
        .andExpect(jsonPath("$.data.appliedInfo[0].user.orgId").value(20));
  }

  private MeetingService.ApplyDetail application(Long id, Long userId) {
    return new MeetingService.ApplyDetail(
        new MeetingApply(
            id,
            MeetingApplyType.APPLY,
            1L,
            userId,
            "신청합니다",
            CREATED_AT,
            MeetingApplyStatus.APPROVE,
            CREATED_AT,
            CREATED_AT),
        profile(userId));
  }

  private MeetingUser profile(Long id) {
    return new MeetingUser(
        id,
        "모임장",
        "leader.png",
        "01012345678",
        List.of(
            Activity.of(35, Team.MAKERS, Part.WEB, true),
            Activity.of(36, Team.MAKERS, Part.SERVER, true)));
  }

  private MeetingFacade.MeetingDetailResult detail(
      boolean host, List<MeetingService.ApplyDetail> applies) {
    return new MeetingFacade.MeetingDetailResult(
        new MeetingService.MeetingDetail(
            meeting(),
            profile(USER_ID),
            List.of(new MeetingUser(30L, "공동 모임장", "co-leader.png", List.of())),
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
