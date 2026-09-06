package org.sopt.makers.api.controller.app.soptamp.dto;

import jakarta.validation.constraints.NotNull;

public record EditProfileMessageRequest(
    @NotNull(message = "profileMessage may not be null") String profileMessage) {}
