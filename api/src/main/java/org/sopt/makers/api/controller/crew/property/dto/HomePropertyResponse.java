package org.sopt.makers.api.controller.crew.property.dto;

import java.util.List;
import org.sopt.makers.domain.crew.property.HomeContent;

public record HomePropertyResponse(List<HomeContentResponse> home) {

  public static HomePropertyResponse from(List<HomeContent> contents) {
    return new HomePropertyResponse(contents.stream().map(HomeContentResponse::from).toList());
  }

  public record HomeContentResponse(String title, List<Long> meetingIds) {

    public static HomeContentResponse from(HomeContent content) {
      return new HomeContentResponse(content.title(), content.meetingIds());
    }
  }
}
