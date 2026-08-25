package org.sopt.makers.domain.crew.notification.service;

import java.util.List;
import org.sopt.makers.domain.crew.meeting.Meeting;
import org.sopt.makers.domain.crew.meeting.tag.MeetingKeywordType;
import org.sopt.makers.domain.crew.notification.MeetingKeywordNotificationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class MeetingKeywordNotificationPublisher {

  private final ApplicationEventPublisher eventPublisher;

  public MeetingKeywordNotificationPublisher(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  public void publish(Meeting meeting, List<MeetingKeywordType> meetingKeywordTypes) {
    if (meetingKeywordTypes == null || meetingKeywordTypes.isEmpty()) {
      return;
    }
    eventPublisher.publishEvent(
        new MeetingKeywordNotificationEvent(
            meetingKeywordTypes, meeting.id(), meeting.title(), meeting.category()));
  }
}
