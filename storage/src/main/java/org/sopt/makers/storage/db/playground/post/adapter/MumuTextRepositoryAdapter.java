package org.sopt.makers.storage.db.playground.post.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.post.mumu.MumuText;
import org.sopt.makers.domain.playground.post.port.MumuTextRepositoryPort;
import org.sopt.makers.storage.db.playground.post.entity.MumuTextEntity;
import org.sopt.makers.storage.db.playground.post.repository.MumuTextJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MumuTextRepositoryAdapter implements MumuTextRepositoryPort {

  private final MumuTextJpaRepository repository;

  @Override
  public MumuText save(MumuText mumuText) {
    return repository.save(MumuTextEntity.fromDomain(mumuText)).toDomain();
  }

  @Override
  public Optional<MumuText> findById(Long id) {
    return repository.findById(id).map(MumuTextEntity::toDomain);
  }

  @Override
  public Optional<MumuText> findActiveAt(LocalDateTime dateTime) {
    return repository
        .findFirstByShowStartDateLessThanEqualAndShowEndDateGreaterThanOrderByShowStartDateDesc(
            dateTime, dateTime)
        .map(MumuTextEntity::toDomain);
  }

  @Override
  public List<MumuText> findAll() {
    return repository.findAllByOrderByShowStartDateAscIdAsc().stream()
        .map(MumuTextEntity::toDomain)
        .toList();
  }

  @Override
  public List<MumuText> findOverlapping(
      Long excludedId, LocalDateTime showStartDate, LocalDateTime showEndDate) {
    return repository.findOverlapping(excludedId, showStartDate, showEndDate).stream()
        .map(MumuTextEntity::toDomain)
        .toList();
  }

  @Override
  public void delete(MumuText mumuText) {
    repository.findById(mumuText.id()).ifPresent(repository::delete);
  }
}
