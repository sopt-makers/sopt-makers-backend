package org.sopt.makers.clients.sms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
record GabiaAuthResponse(
    @JsonProperty("access_token") String accessToken, @JsonProperty("expires_in") long expiresIn) {}
