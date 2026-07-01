package org.sopt.makers.domain.official.member.port;

import java.util.List;
import org.sopt.makers.domain.official.member.Member;

public interface MemberRepositoryPort {

  List<Member> saveAll(Integer generationId, List<Member> members);

  void deleteByGenerationId(Integer generationId);

  List<Member> findByGenerationId(Integer generationId);
}
