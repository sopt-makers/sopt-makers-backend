package org.sopt.makers.api.controller.playground.wordchaingame.dto;

import java.util.List;

public record WordChainGameWinnerAllResponse(
    List<WordChainGameWinnerResponse> winners, Boolean hasNext) {}
