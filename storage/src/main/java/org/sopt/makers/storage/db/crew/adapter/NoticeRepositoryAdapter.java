package org.sopt.makers.storage.db.crew.adapter;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.notice.Notice;
import org.sopt.makers.domain.crew.notice.port.NoticeRepositoryPort;
import org.sopt.makers.storage.db.crew.entity.NoticeEntity;
import org.sopt.makers.storage.db.crew.repository.NoticeJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeRepositoryAdapter implements NoticeRepositoryPort {

  private final NoticeJpaRepository repository;

  @Override
  @Transactional
  public Notice save(Notice notice) {
    return repository.save(NoticeEntity.fromDomain(notice)).toDomain();
  }

  @Override
  public List<Notice> findExposedAt(LocalDateTime now) {
    return repository
        .findAllByExposeStartDateLessThanEqualAndExposeEndDateGreaterThanEqualOrderByCreatedDateDesc(
            now, now)
        .stream()
        .map(NoticeEntity::toDomain)
        .toList();
  }
}
