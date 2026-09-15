package org.sopt.makers.domain.crew.meeting.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum MeetingFailure implements FailureCode {
  NOT_FOUND_MEETING(404, "모임을 찾을 수 없습니다."),
  NOT_FOUND_APPLY(404, "모임 신청 정보를 찾을 수 없습니다."),
  NOT_FOUND_USER(404, "사용자를 찾을 수 없습니다."),

  FORBIDDEN_MEETING(403, "모임에 대한 권한이 없습니다."),

  INVALID_MEETING_VALUE(400, "모임 입력 값이 올바르지 않습니다."),
  INVALID_MEETING_STATUS(400, "모임 상태 값이 올바르지 않습니다."),
  INVALID_MEETING_CATEGORY(400, "모임 카테고리가 올바르지 않습니다."),
  INVALID_APPLY_STATUS(400, "모임 신청 상태 값이 올바르지 않습니다."),
  INVALID_APPLY_TYPE(400, "모임 신청 타입 값이 올바르지 않습니다."),
  INVALID_MEMBER_ROLE(400, "모임 멤버 역할이 올바르지 않습니다."),
  ALREADY_APPLIED_MEETING(400, "이미 신청한 모임입니다."),
  ALREADY_PROCESSED_APPLY(400, "이미 처리된 모임 신청입니다."),
  FULL_MEETING_CAPACITY(400, "모임 정원이 가득 찼습니다."),
  LEADER_CANNOT_APPLY(400, "모임장은 본인 모임에 신청할 수 없습니다."),
  CO_LEADER_CANNOT_APPLY(400, "공동 모임장은 본인 모임에 신청할 수 없습니다."),
  LEADER_CANNOT_BE_CO_LEADER(400, "모임장은 공동 모임장이 될 수 없습니다."),
  NOT_IN_APPLY_PERIOD(400, "모임 신청 기간이 아닙니다."),
  MISSING_USER_ACTIVITY(400, "사용자의 활동 기수/파트 정보가 없습니다."),
  NOT_ACTIVE_GENERATION(400, "활동 기수만 신청할 수 있는 모임입니다."),
  NOT_TARGET_PART(400, "신청 가능한 파트가 아닙니다.");

  private final int statusCode;
  private final String message;
}
