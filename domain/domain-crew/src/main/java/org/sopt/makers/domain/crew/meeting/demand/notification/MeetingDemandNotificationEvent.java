package org.sopt.makers.domain.crew.meeting.demand.notification;

public record MeetingDemandNotificationEvent(
    MeetingDemandNotification notification, Long openedNotificationId) {}
