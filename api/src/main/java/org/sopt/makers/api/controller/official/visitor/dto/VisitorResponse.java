package org.sopt.makers.api.controller.official.visitor.dto;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class VisitorResponse {

  public record TodayCount(int count) {

    public static TodayCount of(int count) {
      return new TodayCount(count);
    }
  }
}
