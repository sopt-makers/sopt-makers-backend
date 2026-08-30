package org.sopt.makers.clients.eventbridge;

import static org.sopt.makers.core.constant.TimeExpressionConstant.DATE;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.clients.eventbridge.dto.AlarmScheduleEventBridgeBody;
import org.sopt.makers.clients.eventbridge.dto.AlarmScheduleEventBridgeHeader;
import org.sopt.makers.clients.eventbridge.dto.AlarmScheduleEventBridgeRequest;
import org.sopt.makers.clients.push.PushProperty;
import org.sopt.makers.core.type.ServiceType;
import org.sopt.makers.domain.admin.alarm.Alarm;
import org.sopt.makers.domain.admin.alarm.AlarmLinkType;
import org.sopt.makers.domain.admin.alarm.exception.AlarmException;
import org.sopt.makers.domain.admin.alarm.exception.AlarmFailure;
import org.sopt.makers.domain.admin.alarm.port.AlarmScheduleSenderPort;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.scheduler.SchedulerClient;
import software.amazon.awssdk.services.scheduler.model.CreateScheduleRequest;
import software.amazon.awssdk.services.scheduler.model.FlexibleTimeWindow;
import software.amazon.awssdk.services.scheduler.model.FlexibleTimeWindowMode;
import software.amazon.awssdk.services.scheduler.model.Target;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class AlarmScheduleSenderAdapter implements AlarmScheduleSenderPort {

  private static final String SCHEDULE_TIME_FORMAT = "HH-mm";

  private final SchedulerClient schedulerClient;
  private final ObjectMapper objectMapper;
  private final PushProperty pushProperty;
  private final EventBridgeProperty eventBridgeProperty;

  @Override
  public void send(Alarm alarm) {
    try {
      String name = buildEventName(alarm);
      String cronExpression = buildCronExpression(alarm);
      String eventJson = buildEventJson(alarm);
      Target target = buildTarget(eventJson);
      schedulerClient.createSchedule(
          CreateScheduleRequest.builder()
              .name(name)
              .target(target)
              .scheduleExpression(cronExpression)
              .actionAfterCompletion("DELETE")
              .flexibleTimeWindow(
                  FlexibleTimeWindow.builder().mode(FlexibleTimeWindowMode.OFF).build())
              .build());
    } catch (Exception e) {
      throw new AlarmException(AlarmFailure.FAIL_SCHEDULE_ALARM);
    }
  }

  private String buildEventName(Alarm alarm) {
    String dateData = alarm.intendedAt().toLocalDate().format(DateTimeFormatter.ofPattern(DATE));
    String timeData =
        alarm.intendedAt().toLocalTime().format(DateTimeFormatter.ofPattern(SCHEDULE_TIME_FORMAT));
    return String.format("%s_%s_%d", dateData, timeData, alarm.id());
  }

  private String buildCronExpression(Alarm alarm) {
    var utc =
        alarm.intendedAt().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
    return String.format(
        "cron(%d %d %d %d ? %d)",
        utc.getMinute(), utc.getHour(), utc.getDayOfMonth(), utc.getMonthValue(), utc.getYear());
  }

  private String buildEventJson(Alarm alarm) throws JacksonException {
    AlarmScheduleEventBridgeHeader header =
        AlarmScheduleEventBridgeHeader.builder()
            .alarmId(alarm.id())
            .action(alarm.target().sendAction().getValue())
            .xApiKey(pushProperty.key())
            .transactionId(UUID.randomUUID().toString())
            .service(ServiceType.ADMIN.getValue())
            .build();

    boolean isAppLink = AlarmLinkType.APP.equals(alarm.content().linkType());
    boolean isWebLink = AlarmLinkType.WEB.equals(alarm.content().linkType());
    AlarmScheduleEventBridgeBody body =
        AlarmScheduleEventBridgeBody.builder()
            .userIds(alarm.target().targetIds())
            .title(alarm.content().title())
            .content(alarm.content().content())
            .category(alarm.content().category())
            .deepLink(isAppLink ? alarm.content().linkPath() : null)
            .webLink(isWebLink ? alarm.content().linkPath() : null)
            .build();

    AlarmScheduleEventBridgeRequest request = AlarmScheduleEventBridgeRequest.of(header, body);
    return String.format("{\"detail\": %s}", objectMapper.writeValueAsString(request));
  }

  private Target buildTarget(String eventJson) {
    return Target.builder()
        .roleArn(eventBridgeProperty.roleArn())
        .arn(pushProperty.arn())
        .input(eventJson)
        .build();
  }
}
