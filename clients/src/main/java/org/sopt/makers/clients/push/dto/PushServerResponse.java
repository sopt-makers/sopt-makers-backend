package org.sopt.makers.clients.push.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PushServerResponse(Integer status, Boolean success, String message) {}
