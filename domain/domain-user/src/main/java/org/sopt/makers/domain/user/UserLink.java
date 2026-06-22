package org.sopt.makers.domain.user;

public record UserLink(Long id, Long userId, String title, String url) {

  public static UserLink of(
      final Long id, final Long userId, final String title, final String url) {
    return new UserLink(id, userId, title, url);
  }
}
