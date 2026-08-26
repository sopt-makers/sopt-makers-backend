package org.sopt.makers.api.controller.crew.meetingdemand;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum MeetingDemandSuccessCode implements SuccessCode {
  GET_MEETING_DEMANDS(200, "모임 수요 목록 조회에 성공했습니다."),
  GET_MEETING_DEMAND(200, "모임 수요 상세 조회에 성공했습니다."),
  GET_OPENED_MEETINGS(200, "모임 수요 기반 개설 모임 조회에 성공했습니다."),
  CREATE_MEETING_DEMAND(201, "모임 수요 생성에 성공했습니다."),
  DELETE_MEETING_DEMAND(200, "모임 수요 삭제에 성공했습니다."),
  SWITCH_MEETING_DEMAND_WAIT(200, "모임 수요 기다려요 상태 변경에 성공했습니다."),
  REPORT_MEETING_DEMAND(201, "모임 수요 신고에 성공했습니다."),
  GET_MEETING_DEMAND_COMMENTS(200, "모임 수요 댓글 조회에 성공했습니다."),
  CREATE_MEETING_DEMAND_COMMENT(201, "모임 수요 댓글 생성에 성공했습니다."),
  UPDATE_MEETING_DEMAND_COMMENT(200, "모임 수요 댓글 수정에 성공했습니다."),
  DELETE_MEETING_DEMAND_COMMENT(200, "모임 수요 댓글 삭제에 성공했습니다."),
  SWITCH_MEETING_DEMAND_COMMENT_LIKE(200, "모임 수요 댓글 좋아요 상태 변경에 성공했습니다."),
  REPORT_MEETING_DEMAND_COMMENT(201, "모임 수요 댓글 신고에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
