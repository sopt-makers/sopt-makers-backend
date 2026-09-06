package org.sopt.makers.domain.app.home;

public final class HtmlTagWrapper {

  private HtmlTagWrapper() {}

  public static String wrapWithTag(String text, String tag) {
    return "<" + tag + ">" + text + "</" + tag + ">";
  }
}
