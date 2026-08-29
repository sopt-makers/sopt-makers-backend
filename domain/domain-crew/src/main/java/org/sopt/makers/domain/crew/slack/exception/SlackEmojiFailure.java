package org.sopt.makers.domain.crew.slack.exception;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.FailureCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum SlackEmojiFailure implements FailureCode {
  INVALID_API_AUTH_TOKEN(400, "Slack API Token이 올바르지 않습니다."),
  INVALID_SLACK_EMOJI_VALUE(400, "Slack 이모지 이벤트 값이 올바르지 않습니다."),
  DUPLICATE_SLACK_EMOJI_MAPPING(400, "이미 존재하는 Slack 이모지 매핑입니다."),
  NOT_FOUND_SLACK_EMOJI_MAPPING(404, "Slack 이모지 매핑을 찾을 수 없습니다."),
  NOT_FOUND_SLACK_MESSAGE_TEMPLATE(404, "Slack 메시지 템플릿을 찾을 수 없습니다."),
  FAIL_SEND_SLACK_MESSAGE(502, "Slack 메시지 전송에 실패했습니다.");

  private final int statusCode;
  private final String message;
}
