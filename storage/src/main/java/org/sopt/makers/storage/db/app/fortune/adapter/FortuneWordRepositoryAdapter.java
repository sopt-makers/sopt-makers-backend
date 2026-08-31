package org.sopt.makers.storage.db.app.fortune.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.fortune.FortuneWord;
import org.sopt.makers.domain.app.fortune.port.FortuneWordRepositoryPort;
import org.sopt.makers.storage.db.app.fortune.entity.FortuneWordEntity;
import org.sopt.makers.storage.db.app.fortune.repository.FortuneWordJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FortuneWordRepositoryAdapter implements FortuneWordRepositoryPort {

  private final FortuneWordJpaRepository fortuneWordJpaRepository;

  @Override
  public Optional<FortuneWord> findById(Long id) {
    return fortuneWordJpaRepository.findById(id).map(FortuneWordEntity::toDomain);
  }

  @Override
  public List<Long> findAllIds() {
    return fortuneWordJpaRepository.findAllIds();
  }
}
