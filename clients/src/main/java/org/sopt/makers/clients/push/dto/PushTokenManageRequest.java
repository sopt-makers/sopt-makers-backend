package org.sopt.makers.clients.push.dto;

import java.util.List;
import org.sopt.makers.domain.app.push.PushToken;

public record PushTokenManageRequest(List<String> userIds, String deviceToken) {

  public static PushTokenManageRequest from(PushToken pushToken) {
    return new PushTokenManageRequest(
        List.of(String.valueOf(pushToken.userId())), pushToken.token());
  }
}
