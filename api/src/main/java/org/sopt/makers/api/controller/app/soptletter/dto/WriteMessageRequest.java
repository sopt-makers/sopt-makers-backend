package org.sopt.makers.api.controller.app.soptletter.dto;

import jakarta.validation.constraints.NotBlank;
import org.sopt.makers.api.common.validation.GraphemeSize;

public record WriteMessageRequest(
    @NotBlank(message = "메시지 내용은 필수입니다.")
        @GraphemeSize(max = 350, message = "메시지는 공백을 포함하여 1자 이상 350자 이하로 작성해야 합니다.")
        String content) {}
