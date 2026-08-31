package org.sopt.makers.domain.crew.slack.exception;

import org.sopt.makers.core.exception.BaseException;

public class SlackEmojiException extends BaseException {

  public SlackEmojiException(SlackEmojiFailure failure) {
    super(failure);
  }
}
