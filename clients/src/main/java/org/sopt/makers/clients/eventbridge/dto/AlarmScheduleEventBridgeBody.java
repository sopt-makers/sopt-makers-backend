package org.sopt.makers.clients.eventbridge.dto;

import java.util.List;
import lombok.Builder;
import org.sopt.makers.domain.admin.alarm.AlarmCategory;

@Builder
public record AlarmScheduleEventBridgeBody(
    List<String> userIds,
    String title,
    String content,
    AlarmCategory category,
    String deepLink,
    String webLink) {}
