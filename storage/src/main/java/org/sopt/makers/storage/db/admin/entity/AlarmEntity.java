package org.sopt.makers.storage.db.admin.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sopt.makers.domain.admin.alarm.Alarm;
import org.sopt.makers.domain.admin.alarm.AlarmCategory;
import org.sopt.makers.domain.admin.alarm.AlarmContent;
import org.sopt.makers.domain.admin.alarm.AlarmLinkType;
import org.sopt.makers.domain.admin.alarm.AlarmSendAction;
import org.sopt.makers.domain.admin.alarm.AlarmStatus;
import org.sopt.makers.domain.admin.alarm.AlarmTarget;
import org.sopt.makers.domain.admin.alarm.AlarmTargetPart;
import org.sopt.makers.domain.admin.alarm.AlarmTargetType;
import org.sopt.makers.domain.admin.alarm.AlarmType;
import org.sopt.makers.storage.db.admin.converter.StringListConverter;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "alarms")
public class AlarmEntity extends BaseEntity {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(value = PROTECTED)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AlarmStatus status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AlarmType type;

  @Enumerated(EnumType.STRING)
  @Column(name = "action", nullable = false)
  private AlarmSendAction sendAction;

  @Enumerated(EnumType.STRING)
  @Column(name = "part", nullable = false)
  private AlarmTargetPart targetPart;

  @Enumerated(EnumType.STRING)
  @Column(name = "target_type", nullable = false)
  private AlarmTargetType targetType;

  @Column(name = "generation")
  private Integer generation;

  @Column(name = "targets", columnDefinition = "TEXT")
  @Convert(converter = StringListConverter.class)
  private List<String> targetIds;

  @Enumerated(EnumType.STRING)
  @Column(name = "category", nullable = false)
  private AlarmCategory category;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "content", columnDefinition = "TEXT")
  private String content;

  @Column(name = "link_path", columnDefinition = "TEXT")
  private String linkPath;

  @Enumerated(EnumType.STRING)
  @Column(name = "link_type", nullable = false)
  private AlarmLinkType linkType;

  @Column(name = "intended_at")
  private LocalDateTime intendedAt;

  @Column(name = "send_at")
  private LocalDateTime sendAt;

  @Builder(access = PRIVATE)
  private AlarmEntity(
      AlarmStatus status,
      AlarmType type,
      AlarmSendAction sendAction,
      AlarmTargetPart targetPart,
      AlarmTargetType targetType,
      Integer generation,
      List<String> targetIds,
      AlarmCategory category,
      String title,
      String content,
      String linkPath,
      AlarmLinkType linkType,
      LocalDateTime intendedAt,
      LocalDateTime sendAt) {
    this.status = status;
    this.type = type;
    this.sendAction = sendAction;
    this.targetPart = targetPart;
    this.targetType = targetType;
    this.generation = generation;
    this.targetIds = targetIds;
    this.category = category;
    this.title = title;
    this.content = content;
    this.linkPath = linkPath;
    this.linkType = linkType;
    this.intendedAt = intendedAt;
    this.sendAt = sendAt;
  }

  public static AlarmEntity from(Alarm alarm) {
    AlarmTarget target = alarm.target();
    AlarmContent content = alarm.content();
    return AlarmEntity.builder()
        .status(alarm.status())
        .type(alarm.type())
        .sendAction(target.sendAction())
        .targetPart(target.targetPart())
        .targetType(target.targetType())
        .generation(target.generation())
        .targetIds(target.targetIds())
        .category(content.category())
        .title(content.title())
        .content(content.content())
        .linkPath(content.linkPath())
        .linkType(content.linkType())
        .intendedAt(alarm.intendedAt())
        .sendAt(alarm.sendAt())
        .build();
  }

  public Alarm toDomain() {
    AlarmTarget target = new AlarmTarget(sendAction, targetPart, targetType, generation, targetIds);
    AlarmContent alarmContent = new AlarmContent(category, title, content, linkPath, linkType);
    return new Alarm(id, status, type, target, alarmContent, intendedAt, sendAt, getCreatedAt());
  }

  public void updateStatus(AlarmStatus newStatus, LocalDateTime successAt) {
    this.status = newStatus;
    this.sendAt = successAt;
  }
}
