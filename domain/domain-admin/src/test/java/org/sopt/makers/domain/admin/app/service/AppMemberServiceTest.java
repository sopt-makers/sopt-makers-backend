package org.sopt.makers.domain.admin.app.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.attendance.*;
import org.sopt.makers.domain.admin.attendance.exception.AttendanceException;
import org.sopt.makers.domain.admin.attendance.port.AttendanceRepositoryPort;
import org.sopt.makers.domain.admin.lecture.LectureAttribute;
import org.sopt.makers.domain.admin.member.Member;
import org.sopt.makers.domain.admin.user.port.AdminUserActivityPort;

class AppMemberServiceTest {
  private final AdminUserActivityPort members = mock(AdminUserActivityPort.class);
  private final AttendanceRepositoryPort attendances = mock(AttendanceRepositoryPort.class);
  private final AppMemberService service = new AppMemberService(members, attendances, 39);
  private final Member member = new Member(10L, "홍길동", 39, Part.SERVER, 1.5f, 3, 2, 1, 1);

  @Test
  @DisplayName("운영 기수의 종료된 세션으로 출석 횟수와 이력을 반환한다")
  void returnsCurrentGenerationSummary() {
    when(members.findMemberByUserIdAndGeneration(10L, 39)).thenReturn(Optional.of(member));
    Attendance seminar = attendance(1L, LectureAttribute.SEMINAR, AttendanceStatus.ATTENDANCE);
    Attendance hidden = attendance(2L, LectureAttribute.ETC, AttendanceStatus.NOT_PARTICIPATE);
    when(attendances.findAllEndedByUserId(10L, 39)).thenReturn(List.of(seminar, hidden));
    var result = service.getMemberTotalAttendance(10L);

    assertThat(result.member()).isEqualTo(member);
    assertThat(result.visibleAttendances()).containsExactly(seminar);
    assertThat(result.countByStatus(AttendanceStatus.ATTENDANCE)).isEqualTo(1);
    assertThat(result.countByStatus(AttendanceStatus.ABSENT)).isZero();
    assertThat(result.countByStatus(AttendanceStatus.TARDY)).isZero();
    assertThat(result.countByStatus(AttendanceStatus.PARTICIPATE)).isZero();
    verify(members, never()).findCurrentActivity(anyLong());
  }

  @Test
  @DisplayName("운영 기수의 저장된 출석 점수를 반환한다")
  void returnsScore() {
    when(members.findMemberByUserIdAndGeneration(10L, 39)).thenReturn(Optional.of(member));
    assertThat(service.getMemberScore(10L)).isEqualTo(1.5f);
    verifyNoInteractions(attendances);
  }

  @Test
  @DisplayName("운영 기수에 활동이 없으면 회원 조회에 실패한다")
  void rejectsInactiveMember() {
    when(members.findMemberByUserIdAndGeneration(10L, 39)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.getMemberTotalAttendance(10L))
        .isInstanceOf(AttendanceException.class);
    assertThatThrownBy(() -> service.getMemberScore(10L)).isInstanceOf(AttendanceException.class);
    verifyNoInteractions(attendances);
  }

  private Attendance attendance(Long id, LectureAttribute attribute, AttendanceStatus status) {
    return new Attendance(
        id,
        10L,
        id,
        "세션",
        LocalDateTime.of(2026, 11, 15, 14, 0),
        attribute,
        true,
        status,
        List.of());
  }
}
