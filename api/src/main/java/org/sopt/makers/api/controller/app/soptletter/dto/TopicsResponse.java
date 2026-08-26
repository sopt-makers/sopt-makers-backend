package org.sopt.makers.api.controller.app.soptletter.dto;

import java.util.List;
import org.sopt.makers.domain.app.soptletter.SoptLetterTopic;

public record TopicsResponse(List<TopicResponse> topics) {

  public static TopicsResponse of(List<SoptLetterTopic> topics) {
    return new TopicsResponse(topics.stream().map(TopicResponse::of).toList());
  }
}
