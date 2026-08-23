package org.sopt.makers.storage.db.playground.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.playground.project.ProjectLink;

@Entity
@Table(name = "links")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectLinkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "title")
    private String title;

    @Column(name = "url")
    private String url;

    private ProjectLinkEntity(Long id, Long projectId, String title, String url) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.url = url;
    }

    public static ProjectLinkEntity from(ProjectLink link) {
        return new ProjectLinkEntity(link.id(), link.projectId(), link.title(), link.url());
    }

    public ProjectLink toDomain() {
        return new ProjectLink(id, projectId, title, url);
    }
}
