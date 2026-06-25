package org.sopt.makers.storage.db.admin.repository;

import java.util.Optional;
import org.sopt.makers.storage.db.admin.entity.AdminAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminAccountJpaRepository extends JpaRepository<AdminAccountEntity, Long> {

  Optional<AdminAccountEntity> findByEmail(String email);

  boolean existsByEmail(String email);
}
