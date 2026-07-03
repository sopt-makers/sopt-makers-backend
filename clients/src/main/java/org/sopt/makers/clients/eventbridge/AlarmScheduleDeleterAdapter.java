package org.sopt.makers.clients.eventbridge;

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

  private static final String DATE_FORMAT = "yyyy-MM-dd";
  private static final String SCHEDULE_TIME_FORMAT = "HH-mm";

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
    String dateData =
        scheduleDateTime.toLocalDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    String timeData =
        scheduleDateTime.toLocalTime().format(DateTimeFormatter.ofPattern(SCHEDULE_TIME_FORMAT));
    return String.format("%s_%s_%d", dateData, timeData, alarmId);
  }
}
