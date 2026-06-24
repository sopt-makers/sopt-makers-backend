package org.sopt.makers.storage.db.admin.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.admin.auth.AdminAccount;
import org.sopt.makers.domain.admin.auth.port.AdminAccountRepositoryPort;
import org.sopt.makers.storage.db.admin.entity.AdminAccountEntity;
import org.sopt.makers.storage.db.admin.repository.AdminAccountJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminAccountRepositoryAdapter implements AdminAccountRepositoryPort {

  private final AdminAccountJpaRepository adminAccountJpaRepository;

  @Transactional
  @Override
  public AdminAccount save(AdminAccount adminAccount) {
    if (adminAccount.id() != null) {
      AdminAccountEntity entity = findEntityByIdOrThrow(adminAccount.id());
      entity.updatePassword(adminAccount.encodedPassword());
      return entity.toDomain();
    }
    return adminAccountJpaRepository.save(AdminAccountEntity.from(adminAccount)).toDomain();
  }

  @Override
  public Optional<AdminAccount> findById(Long id) {
    return adminAccountJpaRepository.findById(id).map(AdminAccountEntity::toDomain);
  }

  @Override
  public Optional<AdminAccount> findByEmail(String email) {
    return adminAccountJpaRepository.findByEmail(email).map(AdminAccountEntity::toDomain);
  }

  @Override
  public boolean existsByEmail(String email) {
    return adminAccountJpaRepository.existsByEmail(email);
  }

  private AdminAccountEntity findEntityByIdOrThrow(Long id) {
    return adminAccountJpaRepository
        .findById(id)
        .orElseThrow(() -> new IllegalStateException("AdminAccountEntity not found: " + id));
  }
}
