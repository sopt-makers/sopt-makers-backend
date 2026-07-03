package org.sopt.makers.domain.admin.alarm;

import java.util.List;

public record AlarmTarget(
    AlarmSendAction sendAction,
    AlarmTargetPart targetPart,
    AlarmTargetType targetType,
    Integer generation,
    List<String> targetIds) {

  public static AlarmTarget all(int generation) {
    return new AlarmTarget(AlarmSendAction.SEND_ALL, AlarmTargetPart.ALL, AlarmTargetType.ALL, generation, null);
  }

  public static AlarmTarget partialForAll(int generation, AlarmTargetPart targetPart, List<String> targetIds) {
    return new AlarmTarget(AlarmSendAction.SEND, targetPart, AlarmTargetType.ALL, generation, targetIds);
  }

  public static AlarmTarget partialForActive(int generation, AlarmTargetPart targetPart, List<String> targetIds) {
    return new AlarmTarget(AlarmSendAction.SEND, targetPart, AlarmTargetType.ACTIVE, generation, targetIds);
  }

  public static AlarmTarget partialForCsv(int generation, List<String> targetIds) {
    return new AlarmTarget(AlarmSendAction.SEND, AlarmTargetPart.UNDEFINED, AlarmTargetType.CSV, generation, targetIds);
  }

  public AlarmTarget withTargetIds(List<String> resolvedIds) {
    return new AlarmTarget(sendAction, targetPart, targetType, generation, resolvedIds);
  }
}
