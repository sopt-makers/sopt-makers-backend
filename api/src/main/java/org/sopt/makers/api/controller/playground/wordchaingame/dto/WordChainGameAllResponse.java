package org.sopt.makers.api.controller.playground.wordchaingame.dto;

import java.util.List;

public record WordChainGameAllResponse(List<WordChainGameRoomResponse> rooms, Boolean hasNext) {}
