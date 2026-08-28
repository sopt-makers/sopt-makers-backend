package org.sopt.makers.domain.crew.slack.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.crew.slack.SlackEmojiMapping;
import org.sopt.makers.domain.crew.slack.SlackMessageTemplate;
import org.sopt.makers.domain.crew.slack.exception.SlackEmojiException;
import org.sopt.makers.domain.crew.slack.port.SlackEmojiMappingRepositoryPort;
import org.sopt.makers.domain.crew.slack.port.SlackMessageSenderPort;
import org.sopt.makers.domain.crew.slack.port.SlackMessageTemplateRepositoryPort;

class SlackEmojiServiceTest {

  private final SlackEmojiMappingRepositoryPort mappingRepository =
      mock(SlackEmojiMappingRepositoryPort.class);
  private final SlackMessageTemplateRepositoryPort templateRepository =
      mock(SlackMessageTemplateRepositoryPort.class);
  private final SlackMessageSenderPort senderPort = mock(SlackMessageSenderPort.class);
  private final SlackEmojiService service =
      new SlackEmojiService(mappingRepository, templateRepository, senderPort);

  @Test
  @DisplayName("호출 이모지와 Slack 사용자 조합이 중복되면 생성을 거부한다")
  void rejectsDuplicateMapping() {
    when(mappingRepository.existsByCallEmojiAndUserSlackId("call_server", "U1")).thenReturn(true);

    assertThatThrownBy(() -> service.addMapping(command("call_server", "U1")))
        .isInstanceOf(SlackEmojiException.class);
  }

  @Test
  @DisplayName("호출 이모지 그룹 전체를 새 이모지로 변경한다")
  void updatesCallEmojiGroup() {
    when(mappingRepository.findAllByCallEmojiForUpdate("call_server"))
        .thenReturn(List.of(mapping(1L, "U1"), mapping(2L, "U2")));

    service.updateCallEmoji("call_server", "call_backend");

    verify(mappingRepository).updateCallEmoji("call_server", "call_backend");
  }

  @Test
  @DisplayName("호출 이모지 그룹 전체를 삭제한다")
  void deletesCallEmojiGroup() {
    when(mappingRepository.findAllByCallEmojiForUpdate("call_server"))
        .thenReturn(List.of(mapping(1L, "U1")));

    service.deleteCallEmoji("call_server");

    verify(mappingRepository).deleteAllByCallEmoji("call_server");
  }

  @Test
  @DisplayName("reaction 이벤트에 호출자와 대상자를 멘션한 스레드 메시지를 발송한다")
  void sendsReactionMentionMessage() {
    when(mappingRepository.findAllByCallEmoji("call_server"))
        .thenReturn(List.of(mapping(1L, "U1"), mapping(2L, "U2")));
    when(templateRepository.findByTemplateCode("call_message"))
        .thenReturn(
            Optional.of(new SlackMessageTemplate("call_message", "{callUser}님이 호출했습니다. {user}")));

    boolean result = service.handleReaction("call_server", "CALLER", "CHANNEL", "123.456");

    assertThat(result).isTrue();
    verify(senderPort).sendThreadMessage("CHANNEL", "123.456", "<@CALLER>님이 호출했습니다. <@U1> <@U2>");
  }

  @Test
  @DisplayName("등록되지 않은 reaction 이벤트는 처리하지 않는다")
  void ignoresUnregisteredReaction() {
    when(mappingRepository.findAllByCallEmoji(any())).thenReturn(List.of());

    assertThat(service.handleReaction("eyes", "CALLER", "CHANNEL", "123.456")).isFalse();
  }

  private SlackEmojiService.AddMappingCommand command(String callEmoji, String userSlackId) {
    return new SlackEmojiService.AddMappingCommand(
        callEmoji, "홍길동", userSlackId, "SERVER", 39, "call_message");
  }

  private SlackEmojiMapping mapping(Long id, String userSlackId) {
    return new SlackEmojiMapping(
        id, "홍길동", userSlackId, "SERVER", 39, "call_server", "call_message");
  }
}
