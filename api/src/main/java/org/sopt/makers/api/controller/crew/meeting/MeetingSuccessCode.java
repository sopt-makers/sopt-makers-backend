package org.sopt.makers.api.controller.crew.meeting;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum MeetingSuccessCode implements SuccessCode {
  CREATE_MEETING(201, "모임 생성에 성공했습니다."),
  UPDATE_MEETING(200, "모임 수정에 성공했습니다."),
  DELETE_MEETING(200, "모임 삭제에 성공했습니다."),
  APPLY_MEETING(201, "모임 신청에 성공했습니다."),
  CANCEL_APPLY_MEETING(200, "모임 신청 취소에 성공했습니다."),
  UPDATE_APPLY_STATUS(200, "모임 신청 상태 변경에 성공했습니다."),
  GET_MEETING(200, "모임 상세 조회에 성공했습니다."),
  GET_MEETINGS(200, "모임 목록 조회에 성공했습니다."),
  GET_MEETING_MEMBERS(200, "모임 멤버 조회에 성공했습니다."),
  GET_MEETING_APPLICANTS(200, "모임 지원자 조회에 성공했습니다."),
  GET_MEETING_PARTICIPANTS(200, "모임 참여자 조회에 성공했습니다."),
  GET_JOINED_MEETINGS(200, "내가 속한 모임 조회에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
