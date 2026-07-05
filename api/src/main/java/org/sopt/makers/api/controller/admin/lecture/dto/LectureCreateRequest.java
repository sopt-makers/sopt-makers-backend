package org.sopt.makers.api.controller.admin.lecture.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @NotNull @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startDate,
    @NotNull @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime endDate,
    @NotNull LectureAttribute attribute) {}
