package org.sopt.makers.storage.db.crew.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.soptmap.SubwayStation;
import org.sopt.makers.domain.crew.soptmap.port.SubwayStationRepositoryPort;
import org.sopt.makers.storage.db.crew.entity.SubwayStationEntity;
import org.sopt.makers.storage.db.crew.querydsl.SubwayStationQuerydslRepository;
import org.sopt.makers.storage.db.crew.repository.SubwayStationJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SubwayStationRepositoryAdapter implements SubwayStationRepositoryPort {

  private final SubwayStationJpaRepository jpaRepository;
  private final SubwayStationQuerydslRepository querydslRepository;

  @Override
  public List<SubwayStation> findAllByNames(List<String> stationNames) {
    return jpaRepository.findAllByNameIn(stationNames).stream()
        .map(SubwayStationEntity::toDomain)
        .toList();
  }

  @Override
  public List<SubwayStation> findAllByIds(List<Long> stationIds) {
    return jpaRepository.findAllById(stationIds).stream()
        .map(SubwayStationEntity::toDomain)
        .toList();
  }

  @Override
  public List<SubwayStation> searchByKeyword(String keyword) {
    return querydslRepository.search(keyword).stream().map(SubwayStationEntity::toDomain).toList();
  }
}
