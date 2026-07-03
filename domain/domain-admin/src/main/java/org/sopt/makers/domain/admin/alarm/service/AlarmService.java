package org.sopt.makers.domain.admin.alarm.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.alarm.Alarm;
import org.sopt.makers.domain.admin.alarm.AlarmCategory;
import org.sopt.makers.domain.admin.alarm.AlarmContent;
import org.sopt.makers.domain.admin.alarm.AlarmLinkType;
import org.sopt.makers.domain.admin.alarm.AlarmStatus;
import org.sopt.makers.domain.admin.alarm.AlarmTarget;
import org.sopt.makers.domain.admin.alarm.AlarmTargetPart;
import org.sopt.makers.domain.admin.alarm.AlarmTargetType;
import org.sopt.makers.domain.admin.alarm.exception.AlarmException;
import org.sopt.makers.domain.admin.alarm.exception.AlarmFailure;
import org.sopt.makers.domain.admin.alarm.port.AlarmInstantSenderPort;
import org.sopt.makers.domain.admin.alarm.port.AlarmMemberQueryPort;
import org.sopt.makers.domain.admin.alarm.port.AlarmRepositoryPort;
import org.sopt.makers.domain.admin.alarm.port.AlarmScheduleDeleterPort;
import org.sopt.makers.domain.admin.alarm.port.AlarmScheduleSenderPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmService {

  private static final String DATE_FORMAT = "yyyy-MM-dd";
  private static final String TIME_FORMAT = "HH:mm";
  private static final String DATETIME_FORMAT = DATE_FORMAT + " " + TIME_FORMAT;

  private final AlarmRepositoryPort alarmRepositoryPort;
  private final AlarmMemberQueryPort alarmMemberQueryPort;
  private final AlarmInstantSenderPort alarmInstantSenderPort;
  private final AlarmScheduleSenderPort alarmScheduleSenderPort;
  private final AlarmScheduleDeleterPort alarmScheduleDeleterPort;

  @Transactional
  public long sendInstantAlarm(SendInstantAlarmCommand command) {
    AlarmTarget target = command.toTarget();
    AlarmContent content = command.toContent();
    Alarm alarm = Alarm.instant(target, content);

    List<String> resolvedIds = extractTargetIds(alarm.target());
    alarm = alarm.withTarget(alarm.target().withTargetIds(resolvedIds));

    alarmInstantSenderPort.send(alarm);
    alarm = alarm.complete(LocalDateTime.now());
    Alarm saved = alarmRepositoryPort.save(alarm);
    return saved.id();
  }

  @Transactional
  public long sendScheduleAlarm(SendScheduleAlarmCommand command) {
    AlarmTarget target = command.toTarget();
    AlarmContent content = command.toContent();
    LocalDateTime intendedAt = parseDateTime(command.postDate(), command.postTime());
    Alarm alarm = Alarm.scheduled(target, content, intendedAt);

    List<String> resolvedIds = extractTargetIds(alarm.target());
    alarm = alarm.withTarget(alarm.target().withTargetIds(resolvedIds));

    Alarm saved = alarmRepositoryPort.save(alarm);
    alarmScheduleSenderPort.send(saved);
    return saved.id();
  }

  public Alarm getAlarm(long alarmId) {
    return alarmRepositoryPort
        .findById(alarmId)
        .orElseThrow(() -> new AlarmException(AlarmFailure.NOT_FOUND_ALARM));
  }

  public AlarmListResult getAlarms(Integer generation, AlarmStatus status, int page, int size) {
    List<Alarm> alarms = alarmRepositoryPort.findAllOrdered(generation, status, page, size);
    int totalCount = alarmRepositoryPort.count(generation, status);
    return new AlarmListResult(alarms, totalCount);
  }

  @Transactional
  public void deleteAlarm(long alarmId) {
    Alarm alarm =
        alarmRepositoryPort
            .findById(alarmId)
            .orElseThrow(() -> new AlarmException(AlarmFailure.NOT_FOUND_ALARM));
    boolean wasScheduled = alarm.isScheduled();
    alarmRepositoryPort.delete(alarm);
    if (wasScheduled) {
      alarmScheduleDeleterPort.delete(alarmId, alarm.intendedAt());
    }
  }

  @Transactional
  public void updateAlarmStatus(long alarmId, String sendAt) {
    Alarm alarm =
        alarmRepositoryPort
            .findById(alarmId)
            .orElseThrow(() -> new AlarmException(AlarmFailure.NOT_FOUND_ALARM));
    LocalDateTime sendAtDateTime =
        LocalDateTime.parse(sendAt, DateTimeFormatter.ofPattern(DATETIME_FORMAT));
    alarmRepositoryPort.save(alarm.complete(sendAtDateTime));
  }

  private List<String> extractTargetIds(AlarmTarget target) {
    AlarmTargetType targetType = target.targetType();
    if (targetType.equals(AlarmTargetType.CSV)) {
      return target.targetIds();
    }

    List<Long> memberIds =
        switch (targetType) {
          case ALL -> alarmMemberQueryPort.findAllUserIds();
          case ACTIVE -> {
            Part part =
                target.targetPart().equals(AlarmTargetPart.ALL)
                    ? null
                    : target.targetPart().toPart();
            yield alarmMemberQueryPort.findActiveUserIdsByGenerationAndPart(
                target.generation(), part != null ? part : Part.ALL);
          }
          default -> throw new AlarmException(AlarmFailure.INVALID_ALARM_TARGET_TYPE);
        };
    return memberIds.stream().filter(Objects::nonNull).map(String::valueOf).toList();
  }

  private LocalDateTime parseDateTime(String date, String time) {
    try {
      LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_FORMAT));
      LocalTime localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern(TIME_FORMAT));
      return LocalDateTime.of(localDate, localTime);
    } catch (Exception e) {
      throw new AlarmException(AlarmFailure.INVALID_SCHEDULE_ALARM_FORMAT);
    }
  }

  public record SendInstantAlarmCommand(
      String title,
      String content,
      AlarmCategory category,
      AlarmTargetType targetType,
      AlarmTargetPart part,
      Integer createdGeneration,
      List<String> targetList,
      AlarmLinkType linkType,
      String link) {

    public AlarmTarget toTarget() {
      return switch (targetType) {
        case ALL ->
            AlarmTargetPart.ALL.equals(part)
                ? AlarmTarget.all(createdGeneration)
                : AlarmTarget.partialForAll(createdGeneration, part, targetList);
        case ACTIVE -> AlarmTarget.partialForActive(createdGeneration, part, targetList);
        case CSV -> AlarmTarget.partialForCsv(createdGeneration, targetList);
      };
    }

    public AlarmContent toContent() {
      return switch (linkType) {
        case WEB -> AlarmContent.withWebLink(title, content, category, link);
        case APP -> AlarmContent.withAppLink(title, content, category, link);
        case NONE -> AlarmContent.withoutLink(title, content, category);
      };
    }
  }

  public record SendScheduleAlarmCommand(
      String title,
      String content,
      AlarmCategory category,
      AlarmTargetType targetType,
      List<String> targetList,
      AlarmTargetPart part,
      Integer createdGeneration,
      AlarmLinkType linkType,
      String link,
      String postDate,
      String postTime) {

    public AlarmTarget toTarget() {
      return switch (targetType) {
        case ALL ->
            AlarmTargetPart.ALL.equals(part)
                ? AlarmTarget.all(createdGeneration)
                : AlarmTarget.partialForAll(createdGeneration, part, targetList);
        case ACTIVE -> AlarmTarget.partialForActive(createdGeneration, part, targetList);
        case CSV -> AlarmTarget.partialForCsv(createdGeneration, targetList);
      };
    }

    public AlarmContent toContent() {
      return switch (linkType) {
        case WEB -> AlarmContent.withWebLink(title, content, category, link);
        case APP -> AlarmContent.withAppLink(title, content, category, link);
        case NONE -> AlarmContent.withoutLink(title, content, category);
      };
    }
  }

  public record AlarmListResult(List<Alarm> alarms, int totalCount) {}
}
