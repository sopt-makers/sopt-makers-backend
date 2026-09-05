package org.sopt.makers.api.controller.app.poke.dto;

public record PokeMessageRequest(String message, Boolean isAnonymous) {

  public PokeMessageRequest {
    isAnonymous = Boolean.TRUE.equals(isAnonymous);
  }
}
