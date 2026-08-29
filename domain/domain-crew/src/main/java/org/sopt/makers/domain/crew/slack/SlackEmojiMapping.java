package org.sopt.makers.domain.crew.slack;

public record SlackEmojiMapping(
    Long id,
    String username,
    String userSlackId,
    String team,
    Integer generation,
    String callEmoji,
    String templateCode) {}
