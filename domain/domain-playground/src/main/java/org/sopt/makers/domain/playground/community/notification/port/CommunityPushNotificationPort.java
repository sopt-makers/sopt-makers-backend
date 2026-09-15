package org.sopt.makers.domain.playground.community.notification.port;

import org.sopt.makers.domain.playground.community.notification.CommunityPushNotification;

public interface CommunityPushNotificationPort {

  void send(CommunityPushNotification notification);
}
