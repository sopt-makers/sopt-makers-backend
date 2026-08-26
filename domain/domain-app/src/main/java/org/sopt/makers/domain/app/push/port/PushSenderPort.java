package org.sopt.makers.domain.app.push.port;

import org.sopt.makers.domain.app.push.PushMessage;
import org.sopt.makers.domain.app.push.PushToken;

public interface PushSenderPort {

  void send(PushMessage message);

  void register(PushToken pushToken);

  void delete(PushToken pushToken);
}
