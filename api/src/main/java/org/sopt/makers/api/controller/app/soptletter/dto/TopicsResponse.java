package org.sopt.makers.api.controller.app.soptletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.sopt.makers.domain.app.soptletter.SoptLetterTopic;

public record TopicsResponse(@Schema(description = "주제 목록") List<TopicResponse> topics) {

  public static TopicsResponse of(List<SoptLetterTopic> topics) {
    return new TopicsResponse(topics.stream().map(TopicResponse::of).toList());
  }
}
