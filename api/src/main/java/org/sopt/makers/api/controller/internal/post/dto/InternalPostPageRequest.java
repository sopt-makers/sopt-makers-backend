package org.sopt.makers.api.controller.internal.post.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record InternalPostPageRequest(@Min(1) Integer page, @Min(1) @Max(50) Integer take) {

  public int pageOrDefault() {
    return page == null ? 1 : page;
  }

  public int takeOrDefault() {
    return take == null ? 12 : take;
  }
}
