package org.sopt.makers.domain.playground.member.ask.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum UserAskFailure implements FailureCode {
  NOT_FOUND_ASK(404, "질문을 찾을 수 없습니다"),
  NOT_FOUND_ANSWER(404, "답변을 찾을 수 없습니다"),
  UNAUTHORIZED_ASK_UPDATE(403, "질문 작성자만 수정할 수 있습니다"),
  ANSWERED_ASK_UPDATE_NOT_ALLOWED(400, "답변이 달린 질문은 수정할 수 없습니다"),
  UNAUTHORIZED_ASK_DELETE(403, "질문을 삭제할 권한이 없습니다"),
  ANSWERED_ASK_DELETE_NOT_ALLOWED(400, "답변이 달린 질문은 작성자가 삭제할 수 없습니다"),
  UNAUTHORIZED_ANSWER_CREATE(403, "질문을 받은 사람만 답변할 수 있습니다"),
  ALREADY_ANSWERED_ASK(400, "이미 답변이 작성된 질문입니다"),
  UNAUTHORIZED_ANSWER_UPDATE(403, "답변 작성자만 수정할 수 있습니다"),
  UNAUTHORIZED_ANSWER_DELETE(403, "답변 작성자만 삭제할 수 있습니다"),
  ALREADY_REPORTED_ASK(400, "이미 신고한 질문입니다");

  private final int statusCode;
  private final String message;
}
