package org.sopt.makers.domain.official.project;

import java.util.List;
import org.sopt.makers.domain.official.project.type.ProjectCategory;
import org.sopt.makers.domain.official.project.type.ProjectLinkType;
import org.sopt.makers.domain.official.project.type.ProjectServiceType;

public record Project(
    Long id,
    String name,
    Integer generation,
    ProjectCategory category,
    List<ProjectServiceType> serviceType,
    String summary,
    String logoImage,
    String thumbnailImage,
    boolean isFounding,
    List<ProjectLink> links) {
  public record ProjectLink(ProjectLinkType linkType, String linkUrl) {}
}
