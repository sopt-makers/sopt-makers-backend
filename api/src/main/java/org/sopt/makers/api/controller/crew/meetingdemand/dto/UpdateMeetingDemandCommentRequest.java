package org.sopt.makers.api.controller.crew.meetingdemand.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateMeetingDemandCommentRequest(@NotBlank String contents) {}
