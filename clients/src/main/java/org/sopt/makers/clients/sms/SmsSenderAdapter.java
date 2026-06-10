package org.sopt.makers.clients.sms;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.auth.port.SmsSenderPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmsSenderAdapter implements SmsSenderPort {

  private final GabiaClient gabiaClient;

  @Override
  public void send(final String phoneNumber, final String message) {
    gabiaClient.send(phoneNumber, message);
  }
}
