package org.sopt.makers.domain.crew.meeting.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.crew.meeting.Meeting;
import org.sopt.makers.domain.crew.meeting.MeetingApply;
import org.sopt.makers.domain.crew.meeting.MeetingApplyStatus;
import org.sopt.makers.domain.crew.meeting.MeetingApplyType;
import org.sopt.makers.domain.crew.meeting.MeetingCategory;
import org.sopt.makers.domain.crew.meeting.MeetingStatus;
import org.sopt.makers.domain.crew.meeting.MeetingUser;
import org.sopt.makers.domain.crew.meeting.Member;
import org.sopt.makers.domain.crew.meeting.port.MeetingActiveGenerationPort;
import org.sopt.makers.domain.crew.meeting.port.MeetingApplyRepositoryPort;
import org.sopt.makers.domain.crew.meeting.port.MeetingRepositoryPort;
import org.sopt.makers.domain.crew.meeting.port.MeetingUserPort;
import org.sopt.makers.domain.crew.meeting.port.MemberRepositoryPort;
import org.sopt.makers.domain.crew.meeting.port.PlaygroundMeetingApplicantPort;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;
import org.sopt.makers.domain.user.Activity;

class PlaygroundMeetingAdapterTest {

  private MeetingService meetingService;
  private MeetingRepositoryPort meetingRepository;
  private MemberRepositoryPort memberRepository;
  private MeetingApplyRepositoryPort applyRepository;
  private MeetingUserPort userPort;
  private MeetingActiveGenerationPort activeGenerationPort;
  private PlaygroundMeetingAdapter adapter;

  @BeforeEach
  void setUp() {
    meetingService = mock(MeetingService.class);
    meetingRepository = mock(MeetingRepositoryPort.class);
    memberRepository = mock(MemberRepositoryPort.class);
    applyRepository = mock(MeetingApplyRepositoryPort.class);
    userPort = mock(MeetingUserPort.class);
    activeGenerationPort = mock(MeetingActiveGenerationPort.class);
    adapter =
        new PlaygroundMeetingAdapter(
            meetingService,
            meetingRepository,
            memberRepository,
            applyRepository,
            userPort,
            activeGenerationPort,
            Clock.fixed(Instant.parse("2026-09-05T00:00:00Z"), ZoneOffset.UTC));
  }

  @Test
  @DisplayName("사용자 프로필용 참여 모임을 페이지 정보와 함께 반환한다")
  void findsUserMeetings() {
    Meeting meeting = meeting(1L, 39, LocalDateTime.of(2026, 1, 1, 0, 0));
    MeetingService.MeetingSummary summary =
        new MeetingService.MeetingSummary(
            meeting, Member.leader(1L, 10L), 1, 1, MeetingStatus.RECRUITMENT_COMPLETE);
    MeetingService.JoinedMeeting joined =
        new MeetingService.JoinedMeeting(summary, Member.coLeader(1L, 20L, 10L));
    when(meetingService.findJoinedMeetings(10L, 1, 12))
        .thenReturn(new PageResult<>(List.of(joined), 1, 1, 1, 12, false, false));

    PageResult<org.sopt.makers.domain.crew.meeting.port.PlaygroundMemberCrewPort.MemberCrewInfo>
        result = adapter.findUserMeetings(10L, 1, 12);

    assertThat(result.content())
        .singleElement()
        .satisfies(
            info -> {
              assertThat(info.id()).isEqualTo(1L);
              assertThat(info.meetingLeader()).isTrue();
              assertThat(info.category()).isEqualTo("스터디");
            });
  }

  @Test
  @DisplayName("함께 참여한 일반 멤버를 모임 개설 기수 기준으로 분류한다")
  void findsRelatedUsersByGeneration() {
    Meeting currentMeeting = meeting(2L, 39, LocalDateTime.of(2026, 2, 1, 0, 0));
    Meeting pastMeeting = meeting(1L, 38, LocalDateTime.of(2025, 2, 1, 0, 0));
    when(memberRepository.findAllByUserId(10L))
        .thenReturn(List.of(Member.leader(2L, 10L), Member.participant(1L, 10L)));
    when(meetingRepository.findAllByIds(List.of(2L, 1L)))
        .thenReturn(List.of(currentMeeting, pastMeeting));
    when(memberRepository.findAllByMeetingIds(List.of(2L, 1L)))
        .thenReturn(
            List.of(
                Member.leader(2L, 10L),
                Member.participant(2L, 20L),
                Member.participant(1L, 30L),
                Member.coLeader(2L, 10L, 40L)));
    when(activeGenerationPort.getActiveGeneration()).thenReturn(39);

    var result = adapter.findRelatedUserIds(10L);

    assertThat(result.currentGenerationUserIds()).containsExactly(20L);
    assertThat(result.pastGenerationUserIds()).containsExactly(30L);
  }

  @Test
  @DisplayName("모임 시작보다 가장 먼저 신청한 모임부터 제한 개수만 반환한다")
  void findsFastestAppliedMeetings() {
    Meeting first = meeting(1L, 39, LocalDateTime.of(2026, 12, 1, 0, 0));
    Meeting second = meeting(2L, 39, LocalDateTime.of(2026, 12, 1, 0, 0));
    MeetingApply early = apply(1L, 10L, LocalDateTime.of(2026, 1, 1, 0, 0));
    MeetingApply late = apply(2L, 10L, LocalDateTime.of(2026, 8, 1, 0, 0));
    when(applyRepository.findAllByUserIdAndAppliedDateBetween(
            10L,
            LocalDateTime.of(2026, 1, 1, 0, 0),
            LocalDateTime.of(2026, 12, 31, 23, 59, 59, 999999999)))
        .thenReturn(List.of(late, early));
    when(meetingRepository.findAllByIds(List.of(2L, 1L))).thenReturn(List.of(first, second));

    var result = adapter.findFastestAppliedMeetings(10L, 1, 2026);

    assertThat(result).singleElement().extracting("meetingId").isEqualTo(1L);
  }

  @Test
  @DisplayName("지원자 목록을 상태와 날짜순으로 필터링하고 권한에 따라 내용을 가린다")
  void findsApplicants() {
    Meeting meeting = meeting(1L, 39, LocalDateTime.of(2026, 12, 1, 0, 0));
    MeetingApply older = apply(1L, 20L, LocalDateTime.of(2026, 1, 1, 0, 0));
    MeetingApply newer = apply(1L, 30L, LocalDateTime.of(2026, 2, 1, 0, 0));
    when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
    when(memberRepository.findByMeetingIdAndUserId(1L, 10L))
        .thenReturn(Optional.of(Member.participant(1L, 10L)));
    when(applyRepository.findAllByMeetingIdAndStatuses(1L, List.of(MeetingApplyStatus.WAITING)))
        .thenReturn(List.of(older, newer));
    when(userPort.findAllById(List.of(30L)))
        .thenReturn(
            List.of(
                new MeetingUser(
                    30L,
                    "지원자",
                    "profile",
                    "010-0000-0000",
                    List.of(Activity.of(39, null, Part.SERVER, true)))));
    PlaygroundMeetingApplicantPort.ApplicantQuery query =
        new PlaygroundMeetingApplicantPort.ApplicantQuery(
            1,
            1,
            List.of(MeetingApplyStatus.WAITING),
            PlaygroundMeetingApplicantPort.SortDirection.DESC);

    var result = adapter.findApplicants(1L, 10L, query);

    assertThat(result.totalElements()).isEqualTo(2);
    assertThat(result.content())
        .singleElement()
        .satisfies(
            applicant -> {
              assertThat(applicant.user().id()).isEqualTo(30L);
              assertThat(applicant.content()).isEmpty();
              assertThat(applicant.applyNumber()).isEqualTo(1);
            });
  }

  private Meeting meeting(Long id, int createdGeneration, LocalDateTime recruitmentStart) {
    return new Meeting(
        id,
        null,
        "모임 " + id,
        "부제목",
        MeetingCategory.STUDY,
        List.of(),
        recruitmentStart,
        recruitmentStart.plusMonths(1),
        10,
        "설명",
        null,
        LocalDateTime.of(2026, 9, 1, 0, 0),
        LocalDateTime.of(2026, 10, 1, 0, 0),
        null,
        null,
        false,
        false,
        null,
        createdGeneration,
        null,
        List.of(),
        null,
        null);
  }

  private MeetingApply apply(Long meetingId, Long userId, LocalDateTime appliedDate) {
    return new MeetingApply(
        meetingId * 100 + userId,
        MeetingApplyType.APPLY,
        meetingId,
        userId,
        "신청 내용",
        appliedDate,
        MeetingApplyStatus.WAITING,
        null,
        null);
  }
}
