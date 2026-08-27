package org.sopt.makers.domain.playground.wordchaingame.exception;

import org.sopt.makers.core.exception.BaseException;

public class WordChainGameException extends BaseException {

  public WordChainGameException(WordChainGameFailure failure) {
    super(failure);
  }
}
