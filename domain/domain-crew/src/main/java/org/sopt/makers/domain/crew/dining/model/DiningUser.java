package org.sopt.makers.domain.crew.dining.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DiningUser {
  private final Long id;
  private final String name;
  private final String profileImage;
  private final String phone;
  private final Integer recentGeneration;
}
