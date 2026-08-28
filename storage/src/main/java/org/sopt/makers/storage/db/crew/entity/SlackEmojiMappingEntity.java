package org.sopt.makers.storage.db.crew.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.crew.slack.SlackEmojiMapping;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "makers_user_slack")
public class SlackEmojiMappingEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String username;

  @Column(name = "user_slack_id", nullable = false)
  private String userSlackId;

  @Column(nullable = false)
  private String team;

  @Column(nullable = false)
  private Integer generation;

  @Column(name = "call_emoji", nullable = false)
  private String callEmoji;

  @Column(name = "slack_message_template_cd")
  private String templateCode;

  @Builder(access = PRIVATE)
  private SlackEmojiMappingEntity(
      Long id,
      String username,
      String userSlackId,
      String team,
      Integer generation,
      String callEmoji,
      String templateCode) {
    this.id = id;
    this.username = username;
    this.userSlackId = userSlackId;
    this.team = team;
    this.generation = generation;
    this.callEmoji = callEmoji;
    this.templateCode = templateCode;
  }

  public SlackEmojiMapping toDomain() {
    return new SlackEmojiMapping(
        id, username, userSlackId, team, generation, callEmoji, templateCode);
  }

  public static SlackEmojiMappingEntity fromDomain(SlackEmojiMapping mapping) {
    return SlackEmojiMappingEntity.builder()
        .id(mapping.id())
        .username(mapping.username())
        .userSlackId(mapping.userSlackId())
        .team(mapping.team())
        .generation(mapping.generation())
        .callEmoji(mapping.callEmoji())
        .templateCode(mapping.templateCode())
        .build();
  }
}
