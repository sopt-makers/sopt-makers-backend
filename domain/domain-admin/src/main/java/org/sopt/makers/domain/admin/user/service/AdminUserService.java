package org.sopt.makers.domain.admin.user.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.user.AdminUser;
import org.sopt.makers.domain.admin.user.port.AdminUserQueryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserService {

  private final AdminUserQueryPort adminUserQueryPort;

  public List<AdminUser> getUsers(int generation, Part part, int page, int limit) {
    return adminUserQueryPort.findByGenerationAndPart(generation, part, page, limit);
  }

  public int countUsers(int generation, Part part) {
    return adminUserQueryPort.countByGenerationAndPart(generation, part);
  }
}
