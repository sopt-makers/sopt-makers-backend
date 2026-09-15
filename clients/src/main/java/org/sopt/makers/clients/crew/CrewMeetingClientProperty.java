package org.sopt.makers.clients.crew;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "external.crew")
public record CrewMeetingClientProperty(String url) {}
