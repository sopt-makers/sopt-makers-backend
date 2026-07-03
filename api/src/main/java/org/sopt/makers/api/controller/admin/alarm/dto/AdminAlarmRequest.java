package org.sopt.makers.api.controller.admin.alarm.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.sopt.makers.domain.admin.alarm.AlarmCategory;
import org.sopt.makers.domain.admin.alarm.AlarmLinkType;
import org.sopt.makers.domain.admin.alarm.AlarmTargetPart;
import org.sopt.makers.domain.admin.alarm.AlarmTargetType;
import org.sopt.makers.domain.admin.alarm.service.AlarmService.SendInstantAlarmCommand;
import org.sopt.makers.domain.admin.alarm.service.AlarmService.SendScheduleAlarmCommand;

public sealed interface AdminAlarmRequest permits
    AdminAlarmRequest.InstantSendRequest,
    AdminAlarmRequest.ScheduleSendRequest,
    AdminAlarmRequest.ScheduleStatusUpdateRequest {

  record InstantSendRequest(
      @NotNull String title,
      @NotNull String content,
      @NotNull AlarmCategory category,
      @NotNull AlarmTargetType targetType,
      AlarmTargetPart part,
      Integer createdGeneration,
      List<String> targetList,
      AlarmLinkType linkType,
      String link) implements AdminAlarmRequest {

    public SendInstantAlarmCommand toCommand() {
      return new SendInstantAlarmCommand(title, content, category, targetType, part, createdGeneration, targetList, linkType, link);
    }
  }

  record ScheduleSendRequest(
      @NotNull String title,
      @NotNull String content,
      @NotNull AlarmCategory category,
      @NotNull AlarmTargetType targetType,
      List<String> targetList,
      AlarmTargetPart part,
      Integer createdGeneration,
      AlarmLinkType linkType,
      String link,
      @NotNull String postDate,
      @NotNull String postTime) implements AdminAlarmRequest {

    public SendScheduleAlarmCommand toCommand() {
      return new SendScheduleAlarmCommand(title, content, category, targetType, targetList, part, createdGeneration, linkType, link, postDate, postTime);
    }
  }

  record ScheduleStatusUpdateRequest(@NotNull String sendAt) implements AdminAlarmRequest {}
}
