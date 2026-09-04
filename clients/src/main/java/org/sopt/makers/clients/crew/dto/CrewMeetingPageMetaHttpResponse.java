package org.sopt.makers.clients.crew.dto;

public record CrewMeetingPageMetaHttpResponse(
    Integer page,
    Integer take,
    Integer itemCount,
    Integer pageCount,
    Boolean hasPreviousPage,
    Boolean hasNextPage) {}
