package org.sopt.makers.storage.db.playground.project.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.project.Project;
import org.sopt.makers.domain.playground.project.port.ProjectQueryPort;
import org.sopt.makers.storage.db.playground.project.repository.ProjectQueryRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectQueryAdapter implements ProjectQueryPort {

  private final ProjectQueryRepository projectQueryRepository;

  @Override
  public List<Project> findProjects(
      Integer limit,
      Long cursor,
      String searchWord,
      String category,
      Boolean isAvailable,
      Boolean isFounding,
      Integer generation) {
    return projectQueryRepository.findProjects(
        limit, cursor, searchWord, category, isAvailable, isFounding, generation);
  }

  @Override
  public int countAllProjects(
      String searchWord,
      String category,
      Boolean isAvailable,
      Boolean isFounding,
      Integer generation) {
    return projectQueryRepository.countAllProjects(
        searchWord, category, isAvailable, isFounding, generation);
  }

  @Override
  public int countProjectsExcludeSopkathon(Long memberId) {
    return projectQueryRepository.countProjectsExcludeSopkathon(memberId);
  }

  @Override
  public List<Project> findRandomProjects(int limit) {
    return projectQueryRepository.findRandomProjects(limit);
  }
}
