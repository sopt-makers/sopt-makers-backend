package org.sopt.makers.domain.admin.alarm;

public record AlarmContent(
    AlarmCategory category,
    String title,
    String content,
    String linkPath,
    AlarmLinkType linkType) {

  public static AlarmContent withAppLink(String title, String content, AlarmCategory category, String link) {
    return new AlarmContent(category, title, content, link, AlarmLinkType.APP);
  }

  public static AlarmContent withWebLink(String title, String content, AlarmCategory category, String link) {
    return new AlarmContent(category, title, content, link, AlarmLinkType.WEB);
  }

  public static AlarmContent withoutLink(String title, String content, AlarmCategory category) {
    return new AlarmContent(category, title, content, null, AlarmLinkType.NONE);
  }
}
