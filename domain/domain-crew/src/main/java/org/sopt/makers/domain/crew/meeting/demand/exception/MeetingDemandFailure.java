package org.sopt.makers.domain.crew.meeting.demand.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum MeetingDemandFailure implements FailureCode {
  NOT_FOUND_MEETING_DEMAND(404, "모임 수요를 찾을 수 없습니다."),
  NOT_FOUND_MEETING_DEMAND_COMMENT(404, "모임 수요 댓글을 찾을 수 없습니다."),
  NOT_FOUND_MEETING_DEMAND_USER(404, "사용자를 찾을 수 없습니다."),

  FORBIDDEN_MEETING_DEMAND(403, "모임 수요에 대한 권한이 없습니다."),
  FORBIDDEN_MEETING_DEMAND_COMMENT(403, "모임 수요 댓글에 대한 권한이 없습니다."),

  INVALID_MEETING_DEMAND_VALUE(400, "모임 수요 입력 값이 올바르지 않습니다."),
  INVALID_MEETING_DEMAND_COMMENT_PARENT(400, "모임 수요 대댓글의 부모 댓글이 올바르지 않습니다."),
  WRITER_CANNOT_WAIT_MEETING_DEMAND(400, "작성자는 자신의 모임 수요를 기다릴 수 없습니다."),
  WRITER_CANNOT_REPORT_MEETING_DEMAND(400, "작성자는 자신의 모임 수요를 신고할 수 없습니다."),
  WRITER_CANNOT_REPORT_MEETING_DEMAND_COMMENT(400, "작성자는 자신의 댓글을 신고할 수 없습니다."),
  ALREADY_REPORTED_MEETING_DEMAND(400, "이미 신고한 모임 수요입니다."),
  ALREADY_REPORTED_MEETING_DEMAND_COMMENT(400, "이미 신고한 모임 수요 댓글입니다.");

  private final int statusCode;
  private final String message;
}
