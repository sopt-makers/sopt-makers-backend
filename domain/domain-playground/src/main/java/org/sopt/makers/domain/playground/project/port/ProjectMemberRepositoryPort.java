package org.sopt.makers.domain.playground.project.port;

import java.util.List;
import org.sopt.makers.domain.playground.project.ProjectMember;

public interface ProjectMemberRepositoryPort {

  void saveAll(List<ProjectMember> members);

  List<ProjectMember> findAllByProjectId(Long projectId);

  void deleteAllByProjectId(Long projectId);

  void deleteAll(List<ProjectMember> members);
}
