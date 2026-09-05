package org.sopt.makers.api.controller.app.soptamp.dto;

import jakarta.validation.constraints.Positive;

public record AddClapRequest(@Positive(message = "clapCount must be > 0") int clapCount) {}
