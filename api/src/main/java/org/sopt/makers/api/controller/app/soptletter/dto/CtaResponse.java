package org.sopt.makers.api.controller.app.soptletter.dto;

import java.util.Optional;
import org.sopt.makers.domain.app.soptletter.SoptLetterTopic;

public record CtaResponse(boolean showCta, Long topicId, String ctaText) {

  public static CtaResponse of(Optional<SoptLetterTopic> activeCta) {
    return activeCta
        .map(topic -> new CtaResponse(true, topic.id(), topic.ctaText()))
        .orElseGet(() -> new CtaResponse(false, null, null));
  }
}
