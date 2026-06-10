package org.sopt.makers.api.controller.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetSocialAccountPlatformRequest(@JsonProperty("phone") String phone) {}
