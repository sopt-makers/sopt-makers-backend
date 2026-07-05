package org.sopt.makers.domain.admin.user.port;

import java.util.List;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.user.AdminUser;

public interface AdminUserQueryPort {

  List<AdminUser> findByGenerationAndPart(int generation, Part part, int page, int limit);

  int countByGenerationAndPart(int generation, Part part);
}
