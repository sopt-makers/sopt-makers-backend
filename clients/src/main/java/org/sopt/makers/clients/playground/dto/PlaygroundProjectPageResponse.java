package org.sopt.makers.clients.playground.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record PlaygroundProjectPageResponse(
    List<PlaygroundProjectResponse> projectList,
    boolean hasNext,
    int totalCount
) {
  public PlaygroundProjectPageResponse {
    if (projectList == null) projectList = List.of();
  }
}
