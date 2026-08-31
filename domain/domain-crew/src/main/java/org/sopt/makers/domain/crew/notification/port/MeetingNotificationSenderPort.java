package org.sopt.makers.domain.crew.notification.port;

import org.sopt.makers.domain.crew.notification.MeetingNotification;

public interface MeetingNotificationSenderPort {

  void send(MeetingNotification notification);
}
