package org.sopt.makers.domain.crew.dining.service;

import static org.sopt.makers.domain.crew.dining.exception.DiningFailure.*;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.dining.exception.DiningException;
import org.sopt.makers.domain.crew.dining.model.DiningMeeting;
import org.sopt.makers.domain.crew.dining.model.DiningRound;
import org.sopt.makers.domain.crew.dining.model.enums.DiningPayerStatus;
import org.sopt.makers.domain.crew.dining.port.DiningAttendancePort;
import org.springframework.stereotype.Component;

/**
 * 회식 권한 검증 - 개설자 전용: 회식·회차 수정/삭제, 회차 추가, 마감, 종료, 결제자 승인·거절 - 개설자 + 해당 회차에서 승인된 공동 결제자: 참석자 관리(조회,
 * 추가, 삭제, CSV)
 */
@Component
@RequiredArgsConstructor
public class DiningPermissionValidator {

  private final DiningAttendancePort diningAttendanceRepository;

  public void validateLeader(DiningMeeting meeting, Long userId) {
    meeting.validateMeetingCreator(userId);
  }

  public void validateLeaderOrApprovedPayer(DiningMeeting meeting, DiningRound round, Long userId) {
    if (!isLeaderOrApprovedPayer(meeting, round, userId)) {
      throw new DiningException(FORBIDDEN_EXCEPTION);
    }
  }

  public boolean isLeader(DiningMeeting meeting, Long userId) {
    return meeting.checkMeetingLeader(userId);
  }

  /** 회차 단위 권한. 해당 회차에서 승인된 공동 결제자만 true */
  public boolean isApprovedPayer(DiningRound round, Long userId) {
    return diningAttendanceRepository.existsByRoundIdAndUserIdAndPayerStatus(
        round.getId(), userId, DiningPayerStatus.APPROVED);
  }

  public boolean isLeaderOrApprovedPayer(DiningMeeting meeting, DiningRound round, Long userId) {
    return isLeader(meeting, userId) || isApprovedPayer(round, userId);
  }
}
