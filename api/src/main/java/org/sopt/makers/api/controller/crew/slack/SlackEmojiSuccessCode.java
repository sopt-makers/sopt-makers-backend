package org.sopt.makers.api.controller.crew.slack;

import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.code.SuccessCode;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public enum SlackEmojiSuccessCode implements SuccessCode {
  ADD_SLACK_EMOJI(200, "Slack 이모지 이벤트 생성에 성공했습니다."),
  UPDATE_SLACK_EMOJI(200, "Slack 이모지 이벤트 수정에 성공했습니다."),
  DELETE_SLACK_EMOJI(200, "Slack 이모지 이벤트 삭제에 성공했습니다.");

  private final int statusCode;
  private final String message;
}
