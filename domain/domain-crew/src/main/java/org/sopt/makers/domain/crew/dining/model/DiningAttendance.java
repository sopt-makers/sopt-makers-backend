package org.sopt.makers.domain.crew.dining.model;

import static org.sopt.makers.domain.crew.dining.exception.DiningFailure.*;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.crew.dining.exception.DiningException;
import org.sopt.makers.domain.crew.dining.model.enums.DiningPayerStatus;

/** 회차별 참석 기록 + 공동 결제자 요청 상태 미참석은 행이 없는 상태로 표현한다. (신청취소, 참석자 삭제 시 행 삭제) */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiningAttendance {

  private Long id;

  /** 회차 id */
  private Long roundId;

  /** 참석자 id */
  private Long userId;

  /** 공동 결제자 요청 상태 (null 이면 요청 없음) */
  private DiningPayerStatus payerStatus;

  /** 공동 결제자 요청 시각 */
  private LocalDateTime payerRequestedAt;

  @Builder
  public DiningAttendance(Long roundId, Long userId) {
    this.roundId = roundId;
    this.userId = userId;
  }

  public void requestPayer(LocalDateTime now) {
    if (this.payerStatus != null) {
      throw new DiningException(ALREADY_REQUESTED_DINING_PAYER);
    }
    this.payerStatus = DiningPayerStatus.PENDING;
    this.payerRequestedAt = now;
  }

  public void approvePayer() {
    validatePendingPayer();
    this.payerStatus = DiningPayerStatus.APPROVED;
  }

  /** 결제자 요청 거절 또는 승인 해제. 참석은 유지한다. */
  public void clearPayer() {
    this.payerStatus = null;
    this.payerRequestedAt = null;
  }

  public void validatePendingPayer() {
    if (this.payerStatus != DiningPayerStatus.PENDING) {
      throw new DiningException(NOT_PENDING_DINING_PAYER);
    }
  }

  public void validateApprovedPayer() {
    if (!isApprovedPayer()) {
      throw new DiningException(NOT_APPROVED_DINING_PAYER);
    }
  }

  public boolean isApprovedPayer() {
    return this.payerStatus == DiningPayerStatus.APPROVED;
  }

  public DiningAttendance(
      Long id,
      Long roundId,
      Long userId,
      DiningPayerStatus payerStatus,
      LocalDateTime payerRequestedAt,
      java.time.LocalDateTime createdTimestamp) {
    this.id = id;
    this.roundId = roundId;
    this.userId = userId;
    this.payerStatus = payerStatus;
    this.payerRequestedAt = payerRequestedAt;
    this.createdTimestamp = createdTimestamp;
  }

  private java.time.LocalDateTime createdTimestamp;
}
