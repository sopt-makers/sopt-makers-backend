package org.sopt.makers.storage.db.playground.member.tl.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.member.tl.TlUser;
import org.sopt.makers.domain.playground.member.tl.port.TlUserRepositoryPort;
import org.sopt.makers.storage.db.playground.member.tl.entity.TlUserEntity;
import org.sopt.makers.storage.db.playground.member.tl.repository.TlUserJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TlUserRepositoryAdapter implements TlUserRepositoryPort {

  private final TlUserJpaRepository tlUserJpaRepository;

  @Transactional
  @Override
  public TlUser save(TlUser tlUser) {
    return tlUserJpaRepository.save(TlUserEntity.fromDomain(tlUser)).toDomain();
  }

  @Override
  public Optional<TlUser> findById(Long id) {
    return tlUserJpaRepository.findById(id).map(TlUserEntity::toDomain);
  }

  @Transactional
  @Override
  public void deleteById(Long id) {
    tlUserJpaRepository.deleteById(id);
  }
}
