package org.sopt.makers.clients.crew.dto;

import java.util.List;

public record CrewMeetingPostHttpResponse(
    List<CrewMeetingPostHttpDto> posts, CrewMeetingPageMetaHttpResponse pageMeta) {}
