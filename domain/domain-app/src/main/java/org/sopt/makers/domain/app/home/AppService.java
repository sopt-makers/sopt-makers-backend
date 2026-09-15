package org.sopt.makers.domain.app.home;

import java.time.LocalDateTime;

public record AppService(
    Long id,
    String serviceName,
    boolean activeUser,
    boolean inactiveUser,
    String iconUrl,
    String deepLink,
    LocalDateTime createdAt) {

  public boolean isVisibleTo(ActivityStatus status) {
    return switch (status) {
      case ACTIVE -> activeUser;
      case INACTIVE -> inactiveUser;
      case UNAUTHENTICATED -> false;
    };
  }
}
