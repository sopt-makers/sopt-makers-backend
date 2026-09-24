package org.sopt.makers.api.controller.playground.coffeechat.dto;

import jakarta.validation.constraints.NotNull;

public record CoffeeChatOpenRequest(@NotNull Boolean open) {}
