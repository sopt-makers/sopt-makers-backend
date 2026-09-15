package org.sopt.makers.domain.playground.project.port;

import java.util.List;
import org.sopt.makers.domain.playground.project.ProjectMember;

public interface ProjectMemberRepositoryPort {

  void saveAll(List<ProjectMember> members);

  List<ProjectMember> findAllByProjectId(Long projectId);

  /** 여러 프로젝트에 소속된 팀원 목록을 한 번에 조회한다. */
  List<ProjectMember> findAllByProjectIds(List<Long> projectIds);

  void deleteAllByProjectId(Long projectId);

  void deleteAll(List<ProjectMember> members);
}
