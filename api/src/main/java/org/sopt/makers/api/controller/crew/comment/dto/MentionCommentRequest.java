package org.sopt.makers.api.controller.crew.comment.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record MentionCommentRequest(
    @NotEmpty List<Long> orgIds, @NotNull Long postId, @NotNull String content) {}
