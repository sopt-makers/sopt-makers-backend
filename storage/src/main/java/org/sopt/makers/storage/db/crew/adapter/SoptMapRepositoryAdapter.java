package org.sopt.makers.storage.db.crew.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.pagination.PageQuery;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.crew.soptmap.MapTag;
import org.sopt.makers.domain.crew.soptmap.SoptMap;
import org.sopt.makers.domain.crew.soptmap.SoptMapSearchResult;
import org.sopt.makers.domain.crew.soptmap.SoptMapSortType;
import org.sopt.makers.domain.crew.soptmap.port.SoptMapRepositoryPort;
import org.sopt.makers.storage.db.crew.entity.SoptMapEntity;
import org.sopt.makers.storage.db.crew.querydsl.SoptMapQuerydslRepository;
import org.sopt.makers.storage.db.crew.repository.SoptMapJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoptMapRepositoryAdapter implements SoptMapRepositoryPort {

  private final SoptMapJpaRepository jpaRepository;
  private final SoptMapQuerydslRepository querydslRepository;

  @Override
  @Transactional
  public SoptMap save(SoptMap soptMap) {
    return jpaRepository.save(SoptMapEntity.fromDomain(soptMap)).toDomain();
  }

  @Override
  public Optional<SoptMap> findById(Long soptMapId) {
    return jpaRepository.findById(soptMapId).map(SoptMapEntity::toDomain);
  }

  @Override
  public Optional<SoptMapSearchResult> findDetail(Long userId, Long soptMapId) {
    return querydslRepository.findDetail(userId, soptMapId);
  }

  @Override
  public PageResult<SoptMapSearchResult> search(
      Long userId,
      List<MapTag> mapTags,
      SoptMapSortType sortType,
      List<Long> stationIds,
      PageQuery pageQuery) {
    return querydslRepository.search(userId, mapTags, sortType, stationIds, pageQuery);
  }

  @Override
  public boolean existsById(Long soptMapId) {
    return jpaRepository.existsById(soptMapId);
  }

  @Override
  public boolean existsByPlaceName(String placeName) {
    return jpaRepository.existsByPlaceName(placeName);
  }

  @Override
  public boolean existsByCreatorId(Long creatorId) {
    return jpaRepository.existsByCreatorId(creatorId);
  }

  @Override
  public boolean existsByCreatorIdAndId(Long creatorId, Long soptMapId) {
    return jpaRepository.existsByCreatorIdAndId(creatorId, soptMapId);
  }

  @Override
  public List<SoptMap> findCreatedBetween(LocalDateTime startAt, LocalDateTime endAt) {
    return jpaRepository.findAllByCreatedAtBetweenOrderByIdAsc(startAt, endAt).stream()
        .map(SoptMapEntity::toDomain)
        .toList();
  }

  @Override
  @Transactional
  public void delete(SoptMap soptMap) {
    jpaRepository.delete(SoptMapEntity.fromDomain(soptMap));
  }
}
