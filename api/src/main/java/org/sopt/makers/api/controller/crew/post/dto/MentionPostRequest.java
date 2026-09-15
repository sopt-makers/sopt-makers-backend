package org.sopt.makers.api.controller.crew.post.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record MentionPostRequest(
    @NotEmpty List<Long> orgIds, @NotNull Long postId, @NotEmpty String content) {}
