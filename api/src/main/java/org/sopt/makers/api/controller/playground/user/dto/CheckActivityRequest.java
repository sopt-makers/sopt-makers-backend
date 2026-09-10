package org.sopt.makers.api.controller.playground.user.dto;

import jakarta.validation.constraints.NotNull;

public record CheckActivityRequest(@NotNull Boolean isCheck) {}
