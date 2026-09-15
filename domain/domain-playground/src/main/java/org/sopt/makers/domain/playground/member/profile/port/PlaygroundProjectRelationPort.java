package org.sopt.makers.domain.playground.member.profile.port;

import java.util.List;
import org.sopt.makers.domain.playground.project.Project;

public interface PlaygroundProjectRelationPort {

  List<Project> findProjectsByUserId(Long userId);

  /** 여러 프로젝트에 소속된 팀원 id의 합집합을 조회한다. */
  List<Long> findUserIdsByProjectIds(List<Long> projectIds);
}
