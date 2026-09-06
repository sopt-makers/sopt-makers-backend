package org.sopt.makers.api.controller.playground.member.dto;

import jakarta.validation.constraints.NotNull;

public record CheckActivityRequest(@NotNull Boolean isCheck) {}
