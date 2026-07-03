package org.sopt.makers.clients.eventbridge.dto;

import lombok.Builder;

@Builder
public record AlarmScheduleEventBridgeHeader(
    String action,
    String xApiKey,
    String transactionId,
    String service,
    long alarmId) {}
