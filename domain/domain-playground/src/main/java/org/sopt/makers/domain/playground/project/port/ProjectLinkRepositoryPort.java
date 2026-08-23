package org.sopt.makers.domain.playground.project.port;

import java.util.List;
import org.sopt.makers.domain.playground.project.ProjectLink;

public interface ProjectLinkRepositoryPort {

    void saveAll(List<ProjectLink> links);

    List<ProjectLink> findAllByProjectId(Long projectId);

    List<ProjectLink> findAllByProjectIdIn(List<Long> projectIds);

    void deleteAllByProjectId(Long projectId);
}
