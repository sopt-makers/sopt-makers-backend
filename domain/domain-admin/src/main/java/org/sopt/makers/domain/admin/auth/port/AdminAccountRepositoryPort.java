package org.sopt.makers.domain.admin.auth.port;

import java.util.Optional;
import org.sopt.makers.domain.admin.auth.AdminAccount;

public interface AdminAccountRepositoryPort {

  AdminAccount save(AdminAccount adminAccount);

  Optional<AdminAccount> findById(Long id);

  Optional<AdminAccount> findByEmail(String email);

  boolean existsByEmail(String email);
}
