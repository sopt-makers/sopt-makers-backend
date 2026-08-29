package org.sopt.makers.domain.crew.slack.service;

import static org.sopt.makers.domain.crew.slack.exception.SlackEmojiFailure.DUPLICATE_SLACK_EMOJI_MAPPING;
import static org.sopt.makers.domain.crew.slack.exception.SlackEmojiFailure.INVALID_SLACK_EMOJI_VALUE;
import static org.sopt.makers.domain.crew.slack.exception.SlackEmojiFailure.NOT_FOUND_SLACK_EMOJI_MAPPING;
import static org.sopt.makers.domain.crew.slack.exception.SlackEmojiFailure.NOT_FOUND_SLACK_MESSAGE_TEMPLATE;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.slack.SlackEmojiMapping;
import org.sopt.makers.domain.crew.slack.SlackMessageTemplate;
import org.sopt.makers.domain.crew.slack.exception.SlackEmojiException;
import org.sopt.makers.domain.crew.slack.port.SlackEmojiMappingRepositoryPort;
import org.sopt.makers.domain.crew.slack.port.SlackMessageSenderPort;
import org.sopt.makers.domain.crew.slack.port.SlackMessageTemplateRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SlackEmojiService {

  private static final String CALL_USER_PLACEHOLDER = "{callUser}";
  private static final String CALLED_USER_PLACEHOLDER = "{user}";

  private final SlackEmojiMappingRepositoryPort mappingRepositoryPort;
  private final SlackMessageTemplateRepositoryPort templateRepositoryPort;
  private final SlackMessageSenderPort messageSenderPort;

  @Transactional
  public SlackEmojiMapping addMapping(AddMappingCommand command) {
    validateAddCommand(command);
    if (mappingRepositoryPort.existsByCallEmojiAndUserSlackId(
        command.callEmoji(), command.userSlackId())) {
      throw new SlackEmojiException(DUPLICATE_SLACK_EMOJI_MAPPING);
    }
    return mappingRepositoryPort.save(
        new SlackEmojiMapping(
            null,
            command.username(),
            command.userSlackId(),
            command.team(),
            command.generation(),
            command.callEmoji(),
            command.templateCode()));
  }

  @Transactional
  public void updateCallEmoji(String originalCallEmoji, String updatedCallEmoji) {
    validateText(originalCallEmoji);
    validateText(updatedCallEmoji);
    List<SlackEmojiMapping> mappings =
        mappingRepositoryPort.findAllByCallEmojiForUpdate(originalCallEmoji);
    if (mappings.isEmpty()) {
      throw new SlackEmojiException(NOT_FOUND_SLACK_EMOJI_MAPPING);
    }
    if (originalCallEmoji.equals(updatedCallEmoji)) {
      return;
    }
    boolean duplicate =
        mappings.stream()
            .anyMatch(
                mapping ->
                    mappingRepositoryPort.existsByCallEmojiAndUserSlackId(
                        updatedCallEmoji, mapping.userSlackId()));
    if (duplicate) {
      throw new SlackEmojiException(DUPLICATE_SLACK_EMOJI_MAPPING);
    }
    mappingRepositoryPort.updateCallEmoji(originalCallEmoji, updatedCallEmoji);
  }

  @Transactional
  public void deleteCallEmoji(String callEmoji) {
    validateText(callEmoji);
    if (mappingRepositoryPort.findAllByCallEmojiForUpdate(callEmoji).isEmpty()) {
      throw new SlackEmojiException(NOT_FOUND_SLACK_EMOJI_MAPPING);
    }
    mappingRepositoryPort.deleteAllByCallEmoji(callEmoji);
  }

  public boolean handleReaction(
      String callEmoji, String callerSlackId, String channelId, String threadTimestamp) {
    if (isBlank(callEmoji)
        || isBlank(callerSlackId)
        || isBlank(channelId)
        || isBlank(threadTimestamp)) {
      return false;
    }
    List<SlackEmojiMapping> mappings = mappingRepositoryPort.findAllByCallEmoji(callEmoji);
    if (mappings.isEmpty()) {
      return false;
    }
    String templateCode =
        mappings.stream()
            .map(SlackEmojiMapping::templateCode)
            .filter(Objects::nonNull)
            .filter(code -> !code.isBlank())
            .findFirst()
            .orElseThrow(() -> new SlackEmojiException(NOT_FOUND_SLACK_MESSAGE_TEMPLATE));
    SlackMessageTemplate template =
        templateRepositoryPort
            .findByTemplateCode(templateCode)
            .orElseThrow(() -> new SlackEmojiException(NOT_FOUND_SLACK_MESSAGE_TEMPLATE));
    if (isBlank(template.content())) {
      throw new SlackEmojiException(NOT_FOUND_SLACK_MESSAGE_TEMPLATE);
    }
    String calledUsers =
        mappings.stream()
            .map(SlackEmojiMapping::userSlackId)
            .map(this::mention)
            .distinct()
            .collect(Collectors.joining(" "));
    String message =
        template
            .content()
            .replace(CALL_USER_PLACEHOLDER, mention(callerSlackId))
            .replace(CALLED_USER_PLACEHOLDER, calledUsers);
    messageSenderPort.sendThreadMessage(channelId, threadTimestamp, message);
    return true;
  }

  private String mention(String slackUserId) {
    return "<@" + slackUserId + ">";
  }

  private void validateAddCommand(AddMappingCommand command) {
    if (command == null
        || isBlank(command.callEmoji())
        || isBlank(command.username())
        || isBlank(command.userSlackId())
        || isBlank(command.team())
        || command.generation() == null
        || command.generation() < 1
        || isBlank(command.templateCode())) {
      throw new SlackEmojiException(INVALID_SLACK_EMOJI_VALUE);
    }
  }

  private void validateText(String value) {
    if (isBlank(value)) {
      throw new SlackEmojiException(INVALID_SLACK_EMOJI_VALUE);
    }
  }

  private boolean isBlank(String value) {
    return value == null || value.isBlank();
  }

  public record AddMappingCommand(
      String callEmoji,
      String username,
      String userSlackId,
      String team,
      Integer generation,
      String templateCode) {}
}
