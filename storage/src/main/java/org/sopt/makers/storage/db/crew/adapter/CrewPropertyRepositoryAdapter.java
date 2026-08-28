package org.sopt.makers.storage.db.crew.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.property.CrewProperty;
import org.sopt.makers.domain.crew.property.port.CrewPropertyRepositoryPort;
import org.sopt.makers.storage.db.crew.entity.CrewPropertyEntity;
import org.sopt.makers.storage.db.crew.repository.CrewPropertyJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrewPropertyRepositoryAdapter implements CrewPropertyRepositoryPort {

  private final CrewPropertyJpaRepository crewPropertyJpaRepository;

  @Override
  public Optional<CrewProperty> findByKey(String key) {
    return crewPropertyJpaRepository.findByKey(key).map(CrewPropertyEntity::toDomain);
  }

  @Override
  public List<CrewProperty> findAll() {
    return crewPropertyJpaRepository.findAll().stream().map(CrewPropertyEntity::toDomain).toList();
  }
}
