package org.sopt.makers.clients.sms;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.auth.exception.AuthException;
import org.sopt.makers.domain.auth.exception.AuthFailure;
import org.sopt.makers.domain.auth.port.SmsSenderPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmsSenderAdapter implements SmsSenderPort {

  private final GabiaClient gabiaClient;

  @Override
  public void send(final String phoneNumber, final String message) {
    try {
      gabiaClient.send(phoneNumber, message);
    } catch (RuntimeException e) {
      throw new AuthException(AuthFailure.SMS_SEND_FAILED);
    }
  }
}
