package org.sopt.makers.storage.db.crew.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.crew.slack.SlackMessageTemplate;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "slack_message_template")
public class SlackMessageTemplateEntity {

  @Id
  @Column(name = "template_cd", nullable = false)
  private String templateCode;

  @Column(name = "template_content", columnDefinition = "TEXT")
  private String content;

  public SlackMessageTemplate toDomain() {
    return new SlackMessageTemplate(templateCode, content);
  }
}
