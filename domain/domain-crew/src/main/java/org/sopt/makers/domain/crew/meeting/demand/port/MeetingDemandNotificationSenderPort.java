package org.sopt.makers.domain.crew.meeting.demand.port;

import org.sopt.makers.domain.crew.meeting.demand.notification.MeetingDemandNotification;

public interface MeetingDemandNotificationSenderPort {

  void send(MeetingDemandNotification notification);
}
