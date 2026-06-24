package org.sopt.makers.domain.official.notification.service;

import static org.sopt.makers.domain.official.notification.exception.NotificationFailure.DUPLICATE_NOTIFICATION;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.notification.Notification;
import org.sopt.makers.domain.official.notification.exception.NotificationException;
import org.sopt.makers.domain.official.notification.port.NotificationRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

  private final NotificationRepositoryPort notificationRepositoryPort;

  @Transactional
  public Notification register(RegisterNotificationCommand command) {
    if (notificationRepositoryPort.existsByEmailAndGeneration(
        command.email(), command.generation())) {
      throw new NotificationException(DUPLICATE_NOTIFICATION);
    }
    return notificationRepositoryPort.save(
        Notification.create(command.email(), command.generation()));
  }

  public List<Notification> findByGeneration(Integer generation) {
    return notificationRepositoryPort.findByGeneration(generation);
  }

  public record RegisterNotificationCommand(String email, Integer generation) {}
}
