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

  /** 유저가 팀원으로 참여한 프로젝트 목록을 조회한다. */
  List<Project> findProjectsByUserId(Long userId);
}
