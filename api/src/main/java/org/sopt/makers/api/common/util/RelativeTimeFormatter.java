package org.sopt.makers.api.common.util;

import java.time.Duration;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RelativeTimeFormatter {

  public static String format(LocalDateTime createdAt) {
    Duration duration = Duration.between(createdAt, LocalDateTime.now());

    long seconds = duration.getSeconds();
    if (seconds < 60) {
      return "몇초 전";
    }

    long minutes = seconds / 60;
    if (minutes < 60) {
      return minutes + "분 전";
    }

    long hours = minutes / 60;
    if (hours < 24) {
      return hours + "시간 전";
    }

    long days = hours / 24;
    if (days < 7) {
      return days + "일 전";
    }

    long weeks = days / 7;
    if (weeks < 5) {
      return weeks + "주 전";
    }

    long months = days / 30;
    if (months < 12) {
      return months + "개월 전";
    }

    long years = months / 12;
    return years + "년 전";
  }
}
