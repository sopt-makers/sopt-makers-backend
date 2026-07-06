package org.sopt.makers.api.controller.crew.meeting.dto;

import jakarta.validation.constraints.NotBlank;

public record MeetingJoinInfoRequest(
    @NotBlank(message = "모임 진행 방식은 필수 입력 값입니다.") String meetingType,
    @NotBlank(message = "모임 진행 빈도는 필수 입력 값입니다.") String meetingFrequency) {}
