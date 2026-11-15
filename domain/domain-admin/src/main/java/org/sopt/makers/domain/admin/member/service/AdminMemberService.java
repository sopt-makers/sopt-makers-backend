package org.sopt.makers.domain.admin.member.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.member.Member;
import org.sopt.makers.domain.admin.user.port.AdminUserActivityPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMemberService {

  private final AdminUserActivityPort adminUserActivityPort;

  public List<Member> getMembers(int generation, Part part, int page, int limit) {
    return adminUserActivityPort.findByGenerationAndPart(generation, part, page, limit);
  }

  public int countMembers(int generation, Part part) {
    return adminUserActivityPort.countByGenerationAndPart(generation, part);
  }
}
