package org.sopt.makers.api.controller.admin.lecture.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.lecture.LectureAttribute;

public record LectureCreateRequest(
    @NotNull Part part,
    @NotBlank String name,
    @NotNull Integer generation,
    String place,
    @NotNull LocalDateTime startDate,
    @NotNull LocalDateTime endDate,
    @NotNull LectureAttribute attribute) {}
