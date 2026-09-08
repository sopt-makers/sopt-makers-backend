package org.sopt.makers.domain.playground.member.ask.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.domain.playground.community.anonymous.service.AnonymousNicknameRetriever;
import org.sopt.makers.domain.playground.community.anonymous.service.AnonymousProfileImageRetriever;
import org.sopt.makers.domain.playground.member.ask.UserAnswer;
import org.sopt.makers.domain.playground.member.ask.UserAsk;
import org.sopt.makers.domain.playground.member.ask.exception.UserAskException;
import org.sopt.makers.domain.playground.member.ask.exception.UserAskFailure;
import org.sopt.makers.domain.playground.member.ask.port.AnswerReactionRepositoryPort;
import org.sopt.makers.domain.playground.member.ask.port.AskReactionRepositoryPort;
import org.sopt.makers.domain.playground.member.ask.port.AskReportRepositoryPort;
import org.sopt.makers.domain.playground.member.ask.port.UserAnswerRepositoryPort;
import org.sopt.makers.domain.playground.member.ask.port.UserAskNotificationPort;
import org.sopt.makers.domain.playground.member.ask.port.UserAskRepositoryPort;

/** asker/receiver 권한 매트릭스 검증. 질문 수정/삭제, 답변 생성/수정/삭제 각각의 권한 규칙을 확인한다. */
class UserAskCommandServiceTest {

  private static final Long ASKER_ID = 1L;
  private static final Long RECEIVER_ID = 2L;
  private static final Long STRANGER_ID = 3L;
  private static final Long ASK_ID = 10L;
  private static final Long ANSWER_ID = 20L;

  private final UserAskRepositoryPort userAskRepositoryPort = mock(UserAskRepositoryPort.class);
  private final UserAnswerRepositoryPort userAnswerRepositoryPort =
      mock(UserAnswerRepositoryPort.class);
  private final AskReactionRepositoryPort askReactionRepositoryPort =
      mock(AskReactionRepositoryPort.class);
  private final AnswerReactionRepositoryPort answerReactionRepositoryPort =
      mock(AnswerReactionRepositoryPort.class);
  private final AskReportRepositoryPort askReportRepositoryPort =
      mock(AskReportRepositoryPort.class);
  private final AnonymousNicknameRetriever anonymousNicknameRetriever =
      mock(AnonymousNicknameRetriever.class);
  private final AnonymousProfileImageRetriever anonymousProfileImageRetriever =
      mock(AnonymousProfileImageRetriever.class);
  private final UserAskNotificationPort userAskNotificationPort =
      mock(UserAskNotificationPort.class);

  private UserAskCommandService service;

  @BeforeEach
  void setUp() {
    service =
        new UserAskCommandService(
            userAskRepositoryPort,
            userAnswerRepositoryPort,
            askReactionRepositoryPort,
            answerReactionRepositoryPort,
            askReportRepositoryPort,
            anonymousNicknameRetriever,
            anonymousProfileImageRetriever,
            userAskNotificationPort);
    when(userAskRepositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
  }

  @Test
  @DisplayName("asker는 답변이 달리기 전까지 질문을 수정할 수 있다")
  void askerCanUpdateUnansweredAsk() {
    when(userAskRepositoryPort.findById(ASK_ID)).thenReturn(Optional.of(unansweredAsk()));
    when(userAnswerRepositoryPort.existsByQuestionId(ASK_ID)).thenReturn(false);

    UserAsk updated = service.updateAsk(ASKER_ID, ASK_ID, "수정된 질문", false);

    assertThat(updated.content()).isEqualTo("수정된 질문");
  }

  @Test
  @DisplayName("asker라도 답변이 달린 질문은 수정할 수 없다")
  void askerCannotUpdateAnsweredAsk() {
    when(userAskRepositoryPort.findById(ASK_ID)).thenReturn(Optional.of(unansweredAsk()));
    when(userAnswerRepositoryPort.existsByQuestionId(ASK_ID)).thenReturn(true);

    assertThatThrownBy(() -> service.updateAsk(ASKER_ID, ASK_ID, "수정된 질문", false))
        .isInstanceOf(UserAskException.class)
        .extracting(exception -> ((UserAskException) exception).getError())
        .isEqualTo(UserAskFailure.ANSWERED_ASK_UPDATE_NOT_ALLOWED);
  }

  @Test
  @DisplayName("receiver는 질문을 수정할 수 없다")
  void receiverCannotUpdateAsk() {
    when(userAskRepositoryPort.findById(ASK_ID)).thenReturn(Optional.of(unansweredAsk()));

    assertThatThrownBy(() -> service.updateAsk(RECEIVER_ID, ASK_ID, "수정된 질문", false))
        .isInstanceOf(UserAskException.class)
        .extracting(exception -> ((UserAskException) exception).getError())
        .isEqualTo(UserAskFailure.UNAUTHORIZED_ASK_UPDATE);
  }

  @Test
  @DisplayName("asker는 답변이 없는 질문을 삭제할 수 있다")
  void askerCanDeleteUnansweredAsk() {
    when(userAskRepositoryPort.findById(ASK_ID)).thenReturn(Optional.of(unansweredAsk()));
    when(userAnswerRepositoryPort.existsByQuestionId(ASK_ID)).thenReturn(false);

    service.deleteAsk(ASKER_ID, ASK_ID);

    verify(userAskRepositoryPort).deleteById(ASK_ID);
  }

  @Test
  @DisplayName("asker는 답변이 달린 질문을 삭제할 수 없다")
  void askerCannotDeleteAnsweredAsk() {
    when(userAskRepositoryPort.findById(ASK_ID)).thenReturn(Optional.of(unansweredAsk()));
    when(userAnswerRepositoryPort.existsByQuestionId(ASK_ID)).thenReturn(true);

    assertThatThrownBy(() -> service.deleteAsk(ASKER_ID, ASK_ID))
        .isInstanceOf(UserAskException.class)
        .extracting(exception -> ((UserAskException) exception).getError())
        .isEqualTo(UserAskFailure.ANSWERED_ASK_DELETE_NOT_ALLOWED);
    verify(userAskRepositoryPort, never()).deleteById(any());
  }

  @Test
  @DisplayName("receiver는 답변이 달린 질문도 항상 삭제할 수 있다")
  void receiverCanAlwaysDeleteAskRegardlessOfAnswerState() {
    when(userAskRepositoryPort.findById(ASK_ID)).thenReturn(Optional.of(unansweredAsk()));
    when(userAnswerRepositoryPort.existsByQuestionId(ASK_ID)).thenReturn(true);

    service.deleteAsk(RECEIVER_ID, ASK_ID);

    verify(userAskRepositoryPort).deleteById(ASK_ID);
  }

  @Test
  @DisplayName("asker도 receiver도 아니면 질문을 삭제할 수 없다")
  void strangerCannotDeleteAsk() {
    when(userAskRepositoryPort.findById(ASK_ID)).thenReturn(Optional.of(unansweredAsk()));

    assertThatThrownBy(() -> service.deleteAsk(STRANGER_ID, ASK_ID))
        .isInstanceOf(UserAskException.class)
        .extracting(exception -> ((UserAskException) exception).getError())
        .isEqualTo(UserAskFailure.UNAUTHORIZED_ASK_DELETE);
  }

  @Test
  @DisplayName("receiver만 답변을 작성할 수 있다")
  void onlyReceiverCanCreateAnswer() {
    when(userAskRepositoryPort.findById(ASK_ID)).thenReturn(Optional.of(unansweredAsk()));
    when(userAnswerRepositoryPort.existsByQuestionId(ASK_ID)).thenReturn(false);
    when(userAnswerRepositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    UserAnswer answer = service.createAnswer(RECEIVER_ID, ASK_ID, "답변");

    assertThat(answer.content()).isEqualTo("답변");
  }

  @Test
  @DisplayName("asker는 답변을 작성할 수 없다")
  void askerCannotCreateAnswer() {
    when(userAskRepositoryPort.findById(ASK_ID)).thenReturn(Optional.of(unansweredAsk()));

    assertThatThrownBy(() -> service.createAnswer(ASKER_ID, ASK_ID, "답변"))
        .isInstanceOf(UserAskException.class)
        .extracting(exception -> ((UserAskException) exception).getError())
        .isEqualTo(UserAskFailure.UNAUTHORIZED_ANSWER_CREATE);
  }

  @Test
  @DisplayName("이미 답변이 작성된 질문에는 다시 답변할 수 없다")
  void cannotCreateAnswerForAlreadyAnsweredAsk() {
    when(userAskRepositoryPort.findById(ASK_ID)).thenReturn(Optional.of(unansweredAsk()));
    when(userAnswerRepositoryPort.existsByQuestionId(ASK_ID)).thenReturn(true);

    assertThatThrownBy(() -> service.createAnswer(RECEIVER_ID, ASK_ID, "답변"))
        .isInstanceOf(UserAskException.class)
        .extracting(exception -> ((UserAskException) exception).getError())
        .isEqualTo(UserAskFailure.ALREADY_ANSWERED_ASK);
  }

  @Test
  @DisplayName("답변은 질문의 receiver만 수정할 수 있다")
  void onlyReceiverCanUpdateAnswer() {
    when(userAnswerRepositoryPort.findById(ANSWER_ID)).thenReturn(Optional.of(answer()));
    when(userAskRepositoryPort.findById(ASK_ID)).thenReturn(Optional.of(unansweredAsk()));
    when(userAnswerRepositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    service.updateAnswer(RECEIVER_ID, ANSWER_ID, "수정된 답변");

    verify(userAnswerRepositoryPort).save(any());
  }

  @Test
  @DisplayName("asker는 답변을 수정할 수 없다")
  void askerCannotUpdateAnswer() {
    when(userAnswerRepositoryPort.findById(ANSWER_ID)).thenReturn(Optional.of(answer()));
    when(userAskRepositoryPort.findById(ASK_ID)).thenReturn(Optional.of(unansweredAsk()));

    assertThatThrownBy(() -> service.updateAnswer(ASKER_ID, ANSWER_ID, "수정된 답변"))
        .isInstanceOf(UserAskException.class)
        .extracting(exception -> ((UserAskException) exception).getError())
        .isEqualTo(UserAskFailure.UNAUTHORIZED_ANSWER_UPDATE);
  }

  @Test
  @DisplayName("답변은 질문의 receiver만 삭제할 수 있다")
  void onlyReceiverCanDeleteAnswer() {
    when(userAnswerRepositoryPort.findById(ANSWER_ID)).thenReturn(Optional.of(answer()));
    when(userAskRepositoryPort.findById(ASK_ID)).thenReturn(Optional.of(unansweredAsk()));

    service.deleteAnswer(RECEIVER_ID, ANSWER_ID);

    verify(userAnswerRepositoryPort).deleteById(ANSWER_ID);
  }

  @Test
  @DisplayName("asker는 답변을 삭제할 수 없다")
  void askerCannotDeleteAnswer() {
    when(userAnswerRepositoryPort.findById(ANSWER_ID)).thenReturn(Optional.of(answer()));
    when(userAskRepositoryPort.findById(ASK_ID)).thenReturn(Optional.of(unansweredAsk()));

    assertThatThrownBy(() -> service.deleteAnswer(ASKER_ID, ANSWER_ID))
        .isInstanceOf(UserAskException.class)
        .extracting(exception -> ((UserAskException) exception).getError())
        .isEqualTo(UserAskFailure.UNAUTHORIZED_ANSWER_DELETE);
    verify(userAnswerRepositoryPort, never()).deleteById(any());
  }

  private UserAsk unansweredAsk() {
    return new UserAsk(ASK_ID, RECEIVER_ID, ASKER_ID, "질문", false, null, null, false, null, null);
  }

  private UserAnswer answer() {
    return new UserAnswer(ANSWER_ID, ASK_ID, "답변", null, null);
  }
}
