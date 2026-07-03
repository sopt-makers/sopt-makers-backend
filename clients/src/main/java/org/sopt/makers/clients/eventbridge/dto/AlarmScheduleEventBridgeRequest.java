package org.sopt.makers.clients.eventbridge.dto;

public record AlarmScheduleEventBridgeRequest(
    AlarmScheduleEventBridgeHeader header,
    AlarmScheduleEventBridgeBody body) {

  public static AlarmScheduleEventBridgeRequest of(
      AlarmScheduleEventBridgeHeader header,
      AlarmScheduleEventBridgeBody body) {
    return new AlarmScheduleEventBridgeRequest(header, body);
  }
}
