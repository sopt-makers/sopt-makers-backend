package org.sopt.makers.storage.db.crew.repository;

import java.util.List;
import org.sopt.makers.storage.db.crew.entity.SubwayStationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubwayStationJpaRepository extends JpaRepository<SubwayStationEntity, Long> {

  List<SubwayStationEntity> findAllByNameIn(List<String> names);
}
