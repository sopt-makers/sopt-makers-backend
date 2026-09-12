package org.sopt.makers.api.controller.app.soptletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Optional;
import org.sopt.makers.domain.app.soptletter.SoptLetterTopic;

public record CtaResponse(
    @Schema(description = "CTA를 띄울지 여부. 노출 기간인 주제가 없으면 false", example = "true") boolean showCta,
    @Schema(description = "CTA가 가리키는 주제 아이디. 띄우지 않으면 null", example = "1") Long topicId,
    @Schema(description = "CTA에 띄울 문구. 띄우지 않으면 null", example = "이번 주 솝레터에 답해보세요") String ctaText) {

  public static CtaResponse of(Optional<SoptLetterTopic> activeCta) {
    return activeCta
        .map(topic -> new CtaResponse(true, topic.id(), topic.ctaText()))
        .orElseGet(() -> new CtaResponse(false, null, null));
  }
}
