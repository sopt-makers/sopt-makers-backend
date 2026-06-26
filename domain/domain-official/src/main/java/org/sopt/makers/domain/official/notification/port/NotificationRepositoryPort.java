package org.sopt.makers.domain.official.notification.port;

import java.util.List;
import org.sopt.makers.domain.official.notification.Notification;

public interface NotificationRepositoryPort {

  boolean existsByEmailAndGeneration(String email, Integer generation);

  Notification save(Notification notification);

  List<Notification> findByGeneration(Integer generation);
}
