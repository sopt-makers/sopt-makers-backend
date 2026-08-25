package org.sopt.makers.api.controller.crew.flash.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record FlashBodyRequest(
    @NotNull @Size(min = 1, max = 30) String title,
    @NotNull @Size(min = 1, max = 500) String desc,
    @NotNull String flashTimingType,
    @NotNull String activityStartDate,
    @NotNull String activityEndDate,
    @NotNull String flashPlaceType,
    String flashPlace,
    @NotNull @Min(1) Integer minimumCapacity,
    @NotNull @Min(1) @Max(999) Integer maximumCapacity,
    @NotNull @Size(max = 1) List<String> files) {}
