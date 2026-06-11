package org.sopt.makers.clients.oauth.exception;

import org.sopt.makers.core.exception.BaseException;

public class OAuthException extends BaseException {

  public OAuthException(final OAuthFailure failure) {
    super(failure);
  }
}
