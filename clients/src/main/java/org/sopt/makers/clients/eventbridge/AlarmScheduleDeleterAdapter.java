package org.sopt.makers.clients.eventbridge;

import static org.sopt.makers.core.constant.TimeExpressionConstant.DATE;
import static org.sopt.makers.core.constant.TimeExpressionConstant.FILE_SAFE_TIME;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.admin.alarm.exception.AlarmException;
import org.sopt.makers.domain.admin.alarm.exception.AlarmFailure;
import org.sopt.makers.domain.admin.alarm.port.AlarmScheduleDeleterPort;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.scheduler.SchedulerClient;
import software.amazon.awssdk.services.scheduler.model.DeleteScheduleRequest;

@Component
@RequiredArgsConstructor
public class AlarmScheduleDeleterAdapter implements AlarmScheduleDeleterPort {

  private final SchedulerClient schedulerClient;

  @Override
  public void delete(long alarmId, LocalDateTime scheduleDateTime) {
    try {
      String eventName = buildEventName(alarmId, scheduleDateTime);
      schedulerClient.deleteSchedule(DeleteScheduleRequest.builder().name(eventName).build());
    } catch (RuntimeException e) {
      throw new AlarmException(AlarmFailure.FAIL_DELETE_SCHEDULE_ALARM);
    }
  }

  private String buildEventName(long alarmId, LocalDateTime scheduleDateTime) {
    String dateData = scheduleDateTime.toLocalDate().format(DateTimeFormatter.ofPattern(DATE));
    String timeData =
        scheduleDateTime.toLocalTime().format(DateTimeFormatter.ofPattern(FILE_SAFE_TIME));
    return String.format("%s_%s_%d", dateData, timeData, alarmId);
  }
}
