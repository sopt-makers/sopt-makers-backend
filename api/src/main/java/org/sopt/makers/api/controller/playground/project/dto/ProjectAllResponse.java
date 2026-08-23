package org.sopt.makers.api.controller.playground.project.dto;

import java.util.List;

public record ProjectAllResponse(
    List<ProjectResponse> projectList, Boolean hasNext, Integer totalCount) {}
