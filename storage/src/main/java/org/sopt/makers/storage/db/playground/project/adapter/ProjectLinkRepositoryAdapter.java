package org.sopt.makers.storage.db.playground.project.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.project.ProjectLink;
import org.sopt.makers.domain.playground.project.port.ProjectLinkRepositoryPort;
import org.sopt.makers.storage.db.playground.project.entity.ProjectLinkEntity;
import org.sopt.makers.storage.db.playground.project.repository.ProjectLinkJpaRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectLinkRepositoryAdapter implements ProjectLinkRepositoryPort {

    private final ProjectLinkJpaRepository projectLinkJpaRepository;

    @Override
    public void saveAll(List<ProjectLink> links) {
        projectLinkJpaRepository.saveAll(
                links.stream().map(ProjectLinkEntity::from).toList()
        );
    }

    @Override
    public List<ProjectLink> findAllByProjectId(Long projectId) {
        return projectLinkJpaRepository.findAllByProjectId(projectId).stream()
                .map(ProjectLinkEntity::toDomain)
                .toList();
    }

    @Override
    public List<ProjectLink> findAllByProjectIdIn(List<Long> projectIds) {
        return projectLinkJpaRepository.findAllByProjectIdIn(projectIds).stream()
                .map(ProjectLinkEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteAllByProjectId(Long projectId) {
        projectLinkJpaRepository.deleteAllByProjectId(projectId);
    }
}
