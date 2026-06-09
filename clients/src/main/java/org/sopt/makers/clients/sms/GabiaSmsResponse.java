package org.sopt.makers.clients.sms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
record GabiaSmsResponse(String code, String message, GabiaSmsResponseData data) {}
