package org.sopt.makers.domain.official.project.port;

import java.util.List;
import org.sopt.makers.domain.official.project.Project;
import org.sopt.makers.domain.official.project.ProjectDetail;

public interface ProjectClientPort {
  List<Project> fetchAll();

  ProjectDetail fetchDetail(Long projectId);
}
