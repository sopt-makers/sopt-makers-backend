package org.sopt.makers.api.controller.playground.project.dto;

import org.sopt.makers.domain.playground.project.ProjectLink;

public record ProjectLinkResponse(
        Long linkId,
        String linkTitle,
        String linkUrl
) {
    public static ProjectLinkResponse from(ProjectLink link) {
        return new ProjectLinkResponse(link.id(), link.title(), link.url());
    }
}
