package org.sopt.makers.api.controller.official.notification.dto;

import static lombok.AccessLevel.PRIVATE;

import java.time.LocalDateTime;
import java.util.List;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.official.notification.Notification;

@NoArgsConstructor(access = PRIVATE)
public final class NotificationResponse {

  public record Register(Long id, String email, Integer generation, LocalDateTime createdAt) {

    public static Register of(Notification notification) {
      return new Register(
          notification.id(),
          notification.email(),
          notification.generation(),
          notification.createdAt());
    }
  }

  public record NotificationList(Integer generation, List<String> emailList) {

    public static NotificationList of(Integer generation, List<Notification> notifications) {
      List<String> emailList = notifications.stream().map(Notification::email).toList();
      return new NotificationList(generation, emailList);
    }
  }
}
