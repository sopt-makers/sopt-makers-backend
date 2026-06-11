package org.sopt.makers.clients.sms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
record GabiaSmsResponseData(
    @JsonProperty("BEFORE_SMS_QTY") String beforeSmsQty,
    @JsonProperty("AFTER_SMS_QTY") String afterSmsQty) {}
