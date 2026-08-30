package org.sopt.makers.clients.push.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Collection;
import java.util.List;
import org.sopt.makers.domain.admin.alarm.Alarm;
import org.sopt.makers.domain.admin.alarm.AlarmLinkType;
import org.sopt.makers.domain.admin.alarm.AlarmTargetType;
import org.sopt.makers.domain.app.push.PushMessage;
import org.sopt.makers.domain.crew.meeting.demand.notification.MeetingDemandNotification;
import org.sopt.makers.domain.crew.notification.MeetingNotification;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PushSendRequest(
    Collection<String> userIds,
    String title,
    String content,
    String category,
    String deepLink,
    String webLink) {

  public static PushSendRequest from(PushMessage message) {
    return new PushSendRequest(
        message.userIds().stream()
            .map(String::valueOf)
            .collect(java.util.stream.Collectors.toSet()),
        message.title(),
        message.content(),
        message.category().name(),
        message.deepLink(),
        message.webLink());
  }

  public static PushSendRequest from(Alarm alarm) {
    boolean isTargetAll = AlarmTargetType.ALL.equals(alarm.target().targetType());
    boolean isAppLink = AlarmLinkType.APP.equals(alarm.content().linkType());
    boolean isWebLink = AlarmLinkType.WEB.equals(alarm.content().linkType());
    return new PushSendRequest(
        isTargetAll ? null : alarm.target().targetIds(),
        alarm.content().title(),
        alarm.content().content(),
        alarm.content().category().name(),
        isAppLink ? alarm.content().linkPath() : null,
        isWebLink ? alarm.content().linkPath() : null);
  }

  public static PushSendRequest from(MeetingNotification notification, String crewWebUrl) {
    return new PushSendRequest(
        toUserIds(notification.userIds()),
        notification.title(),
        notification.content(),
        notification.category(),
        null,
        crewWebUrl + "/detail?id=" + notification.meetingId());
  }

  public static PushSendRequest from(MeetingDemandNotification notification, String crewWebUrl) {
    return new PushSendRequest(
        toUserIds(notification.userIds()),
        notification.title(),
        notification.content(),
        notification.category(),
        null,
        crewWebUrl + notification.webPath());
  }

  private static List<String> toUserIds(List<Long> userIds) {
    return userIds.stream().map(String::valueOf).toList();
  }
}
