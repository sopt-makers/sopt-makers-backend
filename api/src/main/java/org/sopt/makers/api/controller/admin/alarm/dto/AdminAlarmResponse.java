package org.sopt.makers.api.controller.admin.alarm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import org.sopt.makers.domain.admin.alarm.Alarm;
import org.sopt.makers.domain.admin.alarm.service.AlarmService.AlarmListResult;

public sealed interface AdminAlarmResponse permits
    AdminAlarmResponse.AlarmCreated,
    AdminAlarmResponse.AlarmDetail,
    AdminAlarmResponse.AlarmList {

  record AlarmCreated(long id) implements AdminAlarmResponse {

    public static AlarmCreated from(long id) {
      return new AlarmCreated(id);
    }
  }

  record AlarmDetail(
      String status,
      String sendType,
      String targetType,
      String targetPart,
      @JsonInclude(Include.NON_NULL) Integer targetGeneration,
      String createDate,
      String createTime,
      @JsonInclude(Include.NON_NULL) String intendDate,
      @JsonInclude(Include.NON_NULL) String intendTime,
      String sendDate,
      String sendTime,
      String title,
      String content,
      String category,
      String link,
      String linkType) implements AdminAlarmResponse {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm";

    public static AlarmDetail from(Alarm alarm) {
      return new AlarmDetail(
          alarm.status().getDescription(),
          alarm.type().getDescription(),
          alarm.target().targetType().getName(),
          alarm.target().targetPart().getName(),
          alarm.target().generation(),
          toDate(alarm.createdAt()), toTime(alarm.createdAt()),
          toDate(alarm.intendedAt()), toTime(alarm.intendedAt()),
          toDate(alarm.sendAt()), toTime(alarm.sendAt()),
          alarm.content().title(),
          alarm.content().content(),
          alarm.content().category().getName(),
          alarm.content().linkPath(),
          alarm.content().linkType().getName());
    }

    private static String toDate(LocalDateTime dt) {
      return dt == null ? null : dt.toLocalDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    private static String toTime(LocalDateTime dt) {
      return dt == null ? null : dt.toLocalTime().format(DateTimeFormatter.ofPattern(TIME_FORMAT));
    }
  }

  record AlarmList(List<AlarmItem> alarms, int totalCount) implements AdminAlarmResponse {

    public static AlarmList from(AlarmListResult result) {
      return new AlarmList(
          result.alarms().stream().map(AlarmItem::from).toList(),
          result.totalCount());
    }

    record AlarmItem(
        long id,
        String status,
        String sendType,
        String targetType,
        @JsonInclude(Include.NON_NULL) String targetPart,
        String category,
        @JsonInclude(Include.NON_NULL) String intendAt,
        String sendAt,
        String title,
        String content) {

      private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm";

      private static AlarmItem from(Alarm alarm) {
        return new AlarmItem(
            alarm.id(),
            alarm.status().getDescription(),
            alarm.type().getDescription(),
            alarm.target().targetType().getName(),
            alarm.target().targetPart().getName(),
            alarm.content().category().getName(),
            toDateTime(alarm.intendedAt()),
            toDateTime(alarm.sendAt()),
            alarm.content().title(),
            alarm.content().content());
      }

      private static String toDateTime(LocalDateTime dt) {
        return Objects.isNull(dt) ? null : dt.format(DateTimeFormatter.ofPattern(DATETIME_FORMAT));
      }
    }
  }
}
