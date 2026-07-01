package org.sopt.makers.domain.official.member;

public record SnsLinks(String email, String linkedin, String github, String behance) {

  public static SnsLinks of(String email, String linkedin, String github, String behance) {
    return new SnsLinks(
        normalize(email), normalize(linkedin), normalize(github), normalize(behance));
  }

  public static SnsLinks empty() {
    return new SnsLinks("", "", "", "");
  }

  private static String normalize(String url) {
    if (url == null || url.isBlank()) {
      return "";
    }
    return url.trim();
  }
}
