package org.sopt.makers.domain.crew.meeting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.sopt.makers.core.pagination.PageQuery;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.crew.meeting.Meeting;
import org.sopt.makers.domain.crew.meeting.MeetingApply;
import org.sopt.makers.domain.crew.meeting.MeetingApplyStatus;
import org.sopt.makers.domain.crew.meeting.MeetingApplyType;
import org.sopt.makers.domain.crew.meeting.MeetingCategory;
import org.sopt.makers.domain.crew.meeting.MeetingSearchCondition;
import org.sopt.makers.domain.crew.meeting.MeetingStatus;
import org.sopt.makers.domain.crew.meeting.MeetingUser;
import org.sopt.makers.domain.crew.meeting.Member;
import org.sopt.makers.domain.crew.meeting.MemberRole;
import org.sopt.makers.domain.crew.meeting.exception.MeetingException;
import org.sopt.makers.domain.crew.meeting.port.MeetingApplyRepositoryPort;
import org.sopt.makers.domain.crew.meeting.port.MeetingRepositoryPort;
import org.sopt.makers.domain.crew.meeting.port.MeetingUserPort;
import org.sopt.makers.domain.crew.meeting.port.MemberRepositoryPort;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.Team;

class MeetingServiceTest {

  private static final Clock CLOCK =
      Clock.fixed(Instant.parse("2026-09-01T03:00:00Z"), ZoneId.of("Asia/Seoul"));

  private final MeetingRepositoryPort meetingRepository = mock(MeetingRepositoryPort.class);
  private final MeetingApplyRepositoryPort applyRepository = mock(MeetingApplyRepositoryPort.class);
  private final MemberRepositoryPort memberRepository = mock(MemberRepositoryPort.class);
  private final MeetingUserPort userPort = mock(MeetingUserPort.class);
  private final MeetingService service =
      new MeetingService(meetingRepository, applyRepository, memberRepository, userPort, CLOCK);

  @Test
  @DisplayName("모임 생성 시 모임장과 공동 모임장을 역할별 Member로 저장한다")
  void createsLeaderAndCoLeaderMembers() {
    Meeting savedMeeting = meeting(1L);
    Member leader = Member.leader(1L, 10L);
    when(userPort.findById(10L))
        .thenReturn(Optional.of(new MeetingUser(10L, "모임장", null, List.of())));
    when(userPort.findAllById(List.of(20L)))
        .thenReturn(List.of(new MeetingUser(20L, "공동 모임장", null, List.of())));
    when(meetingRepository.save(any())).thenReturn(savedMeeting);
    when(memberRepository.findAllByMeetingId(1L)).thenReturn(List.of(leader));

    service.createMeeting(createCommand(List.of(20L)), 10L);

    verify(memberRepository).save(leader);
    verify(memberRepository).saveOrReplaceRole(new Member(1L, 20L, MemberRole.CO_LEADER));
  }

  @Test
  @DisplayName("모임 삭제 시 Member를 먼저 삭제한다")
  void deletesMembersBeforeMeeting() {
    Meeting meeting = meeting(1L);
    when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
    when(memberRepository.findAllByMeetingId(1L)).thenReturn(List.of(Member.leader(1L, 10L)));

    service.deleteMeeting(1L, 10L);

    InOrder inOrder =
        org.mockito.Mockito.inOrder(applyRepository, memberRepository, meetingRepository);
    inOrder.verify(applyRepository).deleteAllByMeetingId(1L);
    inOrder.verify(memberRepository).deleteAllByMeetingId(1L);
    inOrder.verify(meetingRepository).delete(meeting);
  }

  @Test
  @DisplayName("기존 참여자를 공동 모임장으로 지정하면 역할을 교체한다")
  void replacesParticipantRoleWithCoLeader() {
    Meeting meeting = meeting(1L);
    Member participant = Member.participant(1L, 20L);
    when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
    when(meetingRepository.save(any())).thenReturn(meeting);
    when(memberRepository.findAllByMeetingId(1L))
        .thenReturn(List.of(Member.leader(1L, 10L), participant));
    when(userPort.findAllById(List.of(20L)))
        .thenReturn(List.of(new MeetingUser(20L, "참여자", null, List.of())));
    when(applyRepository.findAllByMeetingId(1L))
        .thenReturn(List.of(apply(MeetingApplyStatus.APPROVE)));

    service.updateMeeting(1L, updateCommand(List.of(20L)), 10L);

    verify(memberRepository).saveOrReplaceRole(new Member(1L, 20L, MemberRole.CO_LEADER));
  }

  @Test
  @DisplayName("승인된 공동 모임장을 해제하면 참여자 역할로 복원한다")
  void restoresApprovedCoLeaderToParticipant() {
    Meeting meeting = meeting(1L);
    when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting));
    when(meetingRepository.save(any())).thenReturn(meeting);
    when(memberRepository.findAllByMeetingId(1L))
        .thenReturn(List.of(Member.leader(1L, 10L), new Member(1L, 20L, MemberRole.CO_LEADER)));
    when(applyRepository.findAllByMeetingId(1L))
        .thenReturn(List.of(apply(MeetingApplyStatus.APPROVE)));

    service.updateMeeting(1L, updateCommand(List.of()), 10L);

    verify(memberRepository).saveOrReplaceRole(Member.participant(1L, 20L));
  }

  @Test
  @DisplayName("신청 승인 시 신청자를 PARTICIPANT Member로 저장한다")
  void approvalCreatesParticipantMember() {
    MeetingApply waiting = apply(MeetingApplyStatus.WAITING);
    when(meetingRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(meeting(1L)));
    when(memberRepository.findAllByMeetingId(1L)).thenReturn(List.of(Member.leader(1L, 10L)));
    when(memberRepository.countByMeetingIdAndRole(1L, MemberRole.PARTICIPANT)).thenReturn(0L);
    when(applyRepository.findById(100L)).thenReturn(Optional.of(waiting));
    when(applyRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    service.updateApplyStatus(
        1L, new MeetingService.UpdateApplyStatusCommand(100L, MeetingApplyStatus.APPROVE), 10L);

    verify(memberRepository).save(Member.participant(1L, 20L));
    verify(meetingRepository).findByIdForUpdate(1L);
  }

  @Test
  @DisplayName("이미 운영 역할이 있는 사용자를 승인해도 참여자로 덮어쓰지 않는다")
  void approvalDoesNotReplaceManagerRole() {
    MeetingApply rejected = apply(MeetingApplyStatus.REJECT);
    Member coLeader = new Member(1L, 20L, MemberRole.CO_LEADER);
    when(meetingRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(meeting(1L)));
    when(memberRepository.findAllByMeetingId(1L))
        .thenReturn(List.of(Member.leader(1L, 10L), coLeader));
    when(memberRepository.countByMeetingIdAndRole(1L, MemberRole.PARTICIPANT)).thenReturn(0L);
    when(memberRepository.findByMeetingIdAndUserId(1L, 20L)).thenReturn(Optional.of(coLeader));
    when(applyRepository.findById(100L)).thenReturn(Optional.of(rejected));
    when(applyRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    service.updateApplyStatus(
        1L, new MeetingService.UpdateApplyStatusCommand(100L, MeetingApplyStatus.APPROVE), 10L);

    verify(memberRepository, never()).save(Member.participant(1L, 20L));
  }

  @Test
  @DisplayName("승인된 신청을 거절하면 PARTICIPANT Member를 삭제한다")
  void rejectionDeletesParticipantMember() {
    MeetingApply approved = apply(MeetingApplyStatus.APPROVE);
    when(meetingRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(meeting(1L)));
    when(memberRepository.findAllByMeetingId(1L))
        .thenReturn(List.of(Member.leader(1L, 10L), Member.participant(1L, 20L)));
    when(applyRepository.findById(100L)).thenReturn(Optional.of(approved));
    when(applyRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    service.updateApplyStatus(
        1L, new MeetingService.UpdateApplyStatusCommand(100L, MeetingApplyStatus.REJECT), 10L);

    verify(memberRepository).deleteByMeetingIdAndUserIdAndRole(1L, 20L, MemberRole.PARTICIPANT);
  }

  @Test
  @DisplayName("모임 신청은 모임 행을 잠근 뒤 중복 및 정원을 검증한다")
  void applyLocksMeetingBeforeValidation() {
    when(meetingRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(meeting(1L)));
    when(userPort.findById(20L))
        .thenReturn(
            Optional.of(
                new MeetingUser(
                    20L, "지원자", null, List.of(Activity.of(36, Team.MAKERS, Part.SERVER, true)))));
    when(memberRepository.findAllByMeetingId(1L)).thenReturn(List.of(Member.leader(1L, 10L)));
    when(applyRepository.findAllByMeetingId(1L)).thenReturn(List.of());
    when(applyRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    service.applyGeneralMeeting(new MeetingService.ApplyMeetingCommand(1L, "신청합니다."), 20L);

    verify(meetingRepository).findByIdForUpdate(1L);
    verify(applyRepository).save(any(MeetingApply.class));
  }

  @Test
  @DisplayName("모임장과 공동 모임장은 지원자 목록을 조회할 수 있다")
  void managerGetsApplicants() {
    MeetingApply waiting = apply(MeetingApplyStatus.WAITING);
    when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting(1L)));
    when(memberRepository.findAllByMeetingId(1L))
        .thenReturn(List.of(Member.leader(1L, 10L), new Member(1L, 30L, MemberRole.CO_LEADER)));
    when(applyRepository.findAllByMeetingId(1L)).thenReturn(List.of(waiting));
    when(userPort.findAllById(List.of(20L)))
        .thenReturn(List.of(new MeetingUser(20L, "지원자", null, List.of())));

    List<MeetingService.ApplyDetail> result = service.getApplicants(1L, 30L);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().apply()).isEqualTo(waiting);
    assertThat(result.getFirst().user().id()).isEqualTo(20L);
  }

  @Test
  @DisplayName("모임 구성원은 승인된 참여자 목록을 조회할 수 있다")
  void memberGetsParticipants() {
    Member participant = Member.participant(1L, 20L);
    when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting(1L)));
    when(memberRepository.findAllByMeetingId(1L))
        .thenReturn(List.of(Member.leader(1L, 10L), participant));
    when(userPort.findAllById(List.of(20L)))
        .thenReturn(List.of(new MeetingUser(20L, "참여자", null, List.of())));

    List<MeetingService.MemberDetail> result = service.getParticipants(1L, 20L);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().member()).isEqualTo(participant);
    assertThat(result.getFirst().user().id()).isEqualTo(20L);
  }

  @Test
  @DisplayName("모임에 속하지 않은 사용자는 참여자 목록을 조회할 수 없다")
  void outsiderCannotGetParticipants() {
    when(meetingRepository.findById(1L)).thenReturn(Optional.of(meeting(1L)));
    when(memberRepository.findAllByMeetingId(1L)).thenReturn(List.of(Member.leader(1L, 10L)));

    assertThatThrownBy(() -> service.getParticipants(1L, 99L)).isInstanceOf(MeetingException.class);
  }

  @Test
  @DisplayName("내가 속한 모임과 역할을 함께 조회한다")
  void getsJoinedMeetingsWithRole() {
    Meeting joinedMeeting = meeting(1L);
    Member leader = Member.leader(1L, 10L);
    Member participant = Member.participant(1L, 20L);
    when(meetingRepository.findAllByMemberUserId(20L, new PageQuery(1, 10)))
        .thenReturn(page(joinedMeeting));
    when(applyRepository.findAllByMeetingIds(List.of(1L))).thenReturn(List.of());
    when(memberRepository.findAllByMeetingIdsAndRole(List.of(1L), MemberRole.LEADER))
        .thenReturn(List.of(leader));
    when(memberRepository.findAllByMeetingIdsAndRole(List.of(1L), MemberRole.PARTICIPANT))
        .thenReturn(List.of(participant));
    when(memberRepository.findAllByMeetingIdsAndUserId(List.of(1L), 20L))
        .thenReturn(List.of(participant));

    PageResult<MeetingService.JoinedMeeting> result = service.findJoinedMeetings(20L, 1, 10);

    assertThat(result.content()).hasSize(1);
    assertThat(result.content().getFirst().member().role()).isEqualTo(MemberRole.PARTICIPANT);
  }

  @Test
  @DisplayName("검색어와 카테고리 및 모집 상태로 모임을 필터링한다")
  void searchesMeetingsWithFilters() {
    when(meetingRepository.search(any(), any()))
        .thenReturn(new PageResult<>(List.of(), 0, 0, 1, 10, false, false));

    service.searchMeetings(
        new MeetingService.SearchMeetingsCommand(
            " 러닝 ", MeetingCategory.STUDY, MeetingStatus.APPLY_ABLE),
        1,
        10);

    ArgumentCaptor<MeetingSearchCondition> conditionCaptor =
        ArgumentCaptor.forClass(MeetingSearchCondition.class);
    verify(meetingRepository).search(conditionCaptor.capture(), eq(new PageQuery(1, 10)));
    assertThat(conditionCaptor.getValue().search()).isEqualTo("러닝");
    assertThat(conditionCaptor.getValue().category()).isEqualTo(MeetingCategory.STUDY);
    assertThat(conditionCaptor.getValue().status()).isEqualTo(MeetingStatus.APPLY_ABLE);
    assertThat(conditionCaptor.getValue().now()).isEqualTo(LocalDateTime.of(2026, 9, 1, 12, 0));
  }

  private MeetingService.CreateMeetingCommand createCommand(List<Long> coLeaderUserIds) {
    return new MeetingService.CreateMeetingCommand(
        null,
        "모임",
        null,
        MeetingCategory.STUDY,
        List.of(),
        LocalDateTime.of(2026, 9, 1, 0, 0),
        LocalDateTime.of(2026, 9, 2, 0, 0),
        10,
        "설명",
        null,
        null,
        null,
        null,
        null,
        false,
        false,
        null,
        36,
        null,
        List.of(),
        coLeaderUserIds);
  }

  private MeetingService.UpdateMeetingCommand updateCommand(List<Long> coLeaderUserIds) {
    return new MeetingService.UpdateMeetingCommand(
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        coLeaderUserIds);
  }

  private MeetingApply apply(MeetingApplyStatus status) {
    return new MeetingApply(
        100L,
        MeetingApplyType.APPLY,
        1L,
        20L,
        "신청합니다.",
        LocalDateTime.of(2026, 9, 1, 12, 0),
        status,
        null,
        null);
  }

  private Meeting meeting(Long id) {
    return new Meeting(
        id,
        null,
        "모임",
        null,
        MeetingCategory.STUDY,
        List.of(),
        LocalDateTime.of(2026, 9, 1, 0, 0),
        LocalDateTime.of(2026, 9, 2, 0, 0),
        10,
        "설명",
        null,
        null,
        null,
        null,
        null,
        false,
        false,
        null,
        36,
        null,
        List.of(),
        null,
        null);
  }

  private PageResult<Meeting> page(Meeting meeting) {
    return new PageResult<>(List.of(meeting), 1, 1, 1, 10, false, false);
  }
}
