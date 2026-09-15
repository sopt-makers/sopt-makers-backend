package org.sopt.makers.domain.app.poke;

public record Friend(Long id, Long userId, Long friendUserId, int pokeCount, String anonymousName) {

  public static Friend createNew(Long userId, Long friendUserId, String anonymousName) {
    return new Friend(null, userId, friendUserId, 1, anonymousName);
  }

  public static Friend ofPokeCount(Long userId, Long friendUserId, int pokeCount) {
    return new Friend(null, userId, friendUserId, pokeCount, null);
  }
}
