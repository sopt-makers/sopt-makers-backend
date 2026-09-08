package org.sopt.makers.api.controller.playground.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AskReportRequest(@Schema(description = "신고 사유") String reason) {}
