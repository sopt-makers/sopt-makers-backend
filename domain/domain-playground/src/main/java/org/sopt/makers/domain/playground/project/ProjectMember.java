package org.sopt.makers.domain.playground.project;

public record ProjectMember(
    Long id, Long projectId, Long userId, String role, String description, Boolean isTeamMember) {}
