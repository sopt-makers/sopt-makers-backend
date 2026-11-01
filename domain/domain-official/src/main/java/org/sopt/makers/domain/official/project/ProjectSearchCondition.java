package org.sopt.makers.domain.official.project;

import org.sopt.makers.domain.official.project.type.ProjectCategory;
import org.sopt.makers.domain.official.project.type.ProjectServiceType;
import org.sopt.makers.domain.official.project.type.ProjectSortDirection;
import org.sopt.makers.domain.official.project.type.ProjectSortType;

public record ProjectSearchCondition(
    ProjectCategory category,
    ProjectServiceType serviceType,
    Integer generation,
    Boolean isAvailable,
    Boolean isFounding,
    ProjectSortType sortType,
    ProjectSortDirection sortDirection) {}
