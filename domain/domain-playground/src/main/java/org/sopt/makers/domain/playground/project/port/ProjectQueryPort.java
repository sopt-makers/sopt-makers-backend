package org.sopt.makers.domain.playground.project.port;

import java.util.List;
import org.sopt.makers.domain.playground.project.Project;

public interface ProjectQueryPort {

  List<Project> findProjects(
      Integer limit,
      Long cursor,
      String searchWord,
      String category,
      Boolean isAvailable,
      Boolean isFounding,
      Integer generation);

  int countAllProjects(
      String searchWord,
      String category,
      Boolean isAvailable,
      Boolean isFounding,
      Integer generation);

  int countProjectsExcludeSopkathon(Long memberId);

  List<Project> findRandomProjects(int limit);

  List<Project> findAllProjects();
}
