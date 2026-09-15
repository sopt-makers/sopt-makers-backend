package org.sopt.makers.domain.app.soptamp.support;

import java.util.ArrayList;
import java.util.List;
import org.sopt.makers.domain.app.push.PushMessage;
import org.sopt.makers.domain.app.push.PushToken;
import org.sopt.makers.domain.app.push.port.PushSenderPort;

public final class FakePushSender implements PushSenderPort {

  private final List<PushMessage> sent = new ArrayList<>();

  public List<PushMessage> sent() {
    return sent;
  }

  @Override
  public void send(PushMessage message) {
    sent.add(message);
  }

  @Override
  public void register(PushToken pushToken) {}

  @Override
  public void delete(PushToken pushToken) {}
}
