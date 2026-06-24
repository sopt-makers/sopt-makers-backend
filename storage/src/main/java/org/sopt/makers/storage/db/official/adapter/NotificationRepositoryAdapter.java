package org.sopt.makers.storage.db.official.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.notification.Notification;
import org.sopt.makers.domain.official.notification.port.NotificationRepositoryPort;
import org.sopt.makers.storage.db.official.entity.NotificationEntity;
import org.sopt.makers.storage.db.official.repository.NotificationJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationRepositoryAdapter implements NotificationRepositoryPort {

  private final NotificationJpaRepository notificationJpaRepository;

  @Override
  public boolean existsByEmailAndGeneration(String email, Integer generation) {
    return notificationJpaRepository.existsByEmailAndGeneration(email, generation);
  }

  @Transactional
  @Override
  public Notification save(Notification notification) {
    return notificationJpaRepository
        .save(NotificationEntity.fromDomain(notification))
        .toDomain();
  }

  @Override
  public List<Notification> findByGeneration(Integer generation) {
    return notificationJpaRepository.findByGeneration(generation).stream()
        .map(NotificationEntity::toDomain)
        .toList();
  }
}
