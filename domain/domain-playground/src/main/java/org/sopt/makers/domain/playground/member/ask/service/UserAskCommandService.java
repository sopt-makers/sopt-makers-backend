package org.sopt.makers.domain.playground.member.ask.service;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousNickname;
import org.sopt.makers.domain.playground.community.anonymous.AnonymousProfileImage;
import org.sopt.makers.domain.playground.community.anonymous.service.AnonymousNicknameRetriever;
import org.sopt.makers.domain.playground.community.anonymous.service.AnonymousProfileImageRetriever;
import org.sopt.makers.domain.playground.member.ask.AnswerReaction;
import org.sopt.makers.domain.playground.member.ask.AskReaction;
import org.sopt.makers.domain.playground.member.ask.AskReport;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 에스크 질문/답변 생성·수정·삭제, 리액션 토글, 신고 유스케이스. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAskCommandService {

  private final UserAskRepositoryPort userAskRepositoryPort;
  private final UserAnswerRepositoryPort userAnswerRepositoryPort;
  private final AskReactionRepositoryPort askReactionRepositoryPort;
  private final AnswerReactionRepositoryPort answerReactionRepositoryPort;
  private final AskReportRepositoryPort askReportRepositoryPort;
  private final AnonymousNicknameRetriever anonymousNicknameRetriever;
  private final AnonymousProfileImageRetriever anonymousProfileImageRetriever;
  private final UserAskNotificationPort userAskNotificationPort;

  @Transactional
  public UserAsk createAsk(Long askerId, Long receiverId, String content, Boolean isAnonymous) {
    // if (askerId.equals(receiverId)) { throw new BadRequestException("자기 자신에게 질문할 수 없습니다."); }
    // 레거시부터 자기 자신에게 질문 생성을 막는 검증이 의도적으로 비활성화(주석 처리)되어 있었고, 그 상태를 그대로 유지한다.
    // (단, getMyLatestAnsweredQuestionLocation류 위치 조회에서는 self-check가 여전히 차단 동작한다.)
    Long anonymousNicknameId = null;
    Long anonymousProfileImageId = null;

    if (Boolean.TRUE.equals(isAnonymous)) {
      AnonymousIdentity identity = assignAnonymousIdentity(receiverId);
      anonymousNicknameId = identity.nicknameId();
      anonymousProfileImageId = identity.profileImageId();
    }

    UserAsk created =
        userAskRepositoryPort.save(
            new UserAsk(
                null,
                receiverId,
                askerId,
                content,
                isAnonymous,
                anonymousNicknameId,
                anonymousProfileImageId,
                false,
                null,
                null));

    userAskNotificationPort.sendQuestionNotification(created.id(), receiverId, content);

    return created;
  }

  @Transactional
  public UserAsk updateAsk(Long userId, Long askId, String content, Boolean isAnonymous) {
    UserAsk ask = getAskOrThrow(askId);
    validateAskOwner(ask, userId);
    validateNotAnswered(askId, UserAskFailure.ANSWERED_ASK_UPDATE_NOT_ALLOWED);

    Long anonymousNicknameId = null;
    Long anonymousProfileImageId = null;

    if (Boolean.TRUE.equals(isAnonymous)) {
      if (Boolean.TRUE.equals(ask.isAnonymous())) {
        anonymousNicknameId = ask.anonymousNicknameId();
        anonymousProfileImageId = ask.anonymousProfileImageId();
      } else {
        AnonymousIdentity identity = assignAnonymousIdentity(ask.receiverUserId());
        anonymousNicknameId = identity.nicknameId();
        anonymousProfileImageId = identity.profileImageId();
      }
    }

    return userAskRepositoryPort.save(
        new UserAsk(
            ask.id(),
            ask.receiverUserId(),
            ask.askerUserId(),
            content,
            isAnonymous,
            anonymousNicknameId,
            anonymousProfileImageId,
            ask.isReported(),
            ask.createdAt(),
            ask.updatedAt()));
  }

  @Transactional
  public void deleteAsk(Long userId, Long askId) {
    UserAsk ask = getAskOrThrow(askId);
    boolean isAsker = Objects.equals(ask.askerUserId(), userId);
    boolean isReceiver = Objects.equals(ask.receiverUserId(), userId);

    if (!isAsker && !isReceiver) {
      throw new UserAskException(UserAskFailure.UNAUTHORIZED_ASK_DELETE);
    }
    if (isAsker && userAnswerRepositoryPort.existsByQuestionId(askId)) {
      throw new UserAskException(UserAskFailure.ANSWERED_ASK_DELETE_NOT_ALLOWED);
    }

    userAnswerRepositoryPort
        .findByQuestionId(askId)
        .ifPresent(answer -> answerReactionRepositoryPort.deleteAllByAnswerId(answer.id()));
    userAnswerRepositoryPort.deleteByQuestionId(askId);
    askReactionRepositoryPort.deleteAllByQuestionId(askId);
    askReportRepositoryPort.deleteAllByQuestionId(askId);
    userAskRepositoryPort.deleteById(askId);
  }

  @Transactional
  public UserAnswer createAnswer(Long userId, Long askId, String content) {
    UserAsk ask = getAskOrThrow(askId);
    if (!Objects.equals(ask.receiverUserId(), userId)) {
      throw new UserAskException(UserAskFailure.UNAUTHORIZED_ANSWER_CREATE);
    }
    if (userAnswerRepositoryPort.existsByQuestionId(askId)) {
      throw new UserAskException(UserAskFailure.ALREADY_ANSWERED_ASK);
    }

    UserAnswer created =
        userAnswerRepositoryPort.save(new UserAnswer(null, askId, content, null, null));

    userAskNotificationPort.sendAnswerNotification(askId, ask.askerUserId(), userId, content);

    return created;
  }

  @Transactional
  public void updateAnswer(Long userId, Long answerId, String content) {
    UserAnswer answer = getAnswerOrThrow(answerId);
    UserAsk ask = getAskOrThrow(answer.questionId());
    validateAnswerOwner(ask, userId, UserAskFailure.UNAUTHORIZED_ANSWER_UPDATE);

    userAnswerRepositoryPort.save(
        new UserAnswer(
            answer.id(), answer.questionId(), content, answer.createdAt(), answer.updatedAt()));
  }

  @Transactional
  public void deleteAnswer(Long userId, Long answerId) {
    UserAnswer answer = getAnswerOrThrow(answerId);
    UserAsk ask = getAskOrThrow(answer.questionId());
    validateAnswerOwner(ask, userId, UserAskFailure.UNAUTHORIZED_ANSWER_DELETE);

    answerReactionRepositoryPort.deleteAllByAnswerId(answerId);
    userAnswerRepositoryPort.deleteById(answerId);
  }

  @Transactional
  public void toggleAskReaction(Long userId, Long askId) {
    getAskOrThrow(askId);

    askReactionRepositoryPort
        .findByQuestionIdAndReactorUserId(askId, userId)
        .ifPresentOrElse(
            reaction -> askReactionRepositoryPort.deleteById(reaction.id()),
            () -> askReactionRepositoryPort.save(new AskReaction(null, askId, userId, null, null)));
  }

  @Transactional
  public void toggleAnswerReaction(Long userId, Long answerId) {
    getAnswerOrThrow(answerId);

    answerReactionRepositoryPort
        .findByAnswerIdAndReactorUserId(answerId, userId)
        .ifPresentOrElse(
            reaction -> answerReactionRepositoryPort.deleteById(reaction.id()),
            () ->
                answerReactionRepositoryPort.save(
                    new AnswerReaction(null, answerId, userId, null, null)));
  }

  @Transactional
  public void reportAsk(Long reporterId, Long askId, String reason) {
    UserAsk ask = getAskOrThrow(askId);

    if (askReportRepositoryPort.existsByQuestionIdAndReporterUserId(askId, reporterId)) {
      throw new UserAskException(UserAskFailure.ALREADY_REPORTED_ASK);
    }

    askReportRepositoryPort.save(new AskReport(null, askId, reporterId, reason, null));

    userAskRepositoryPort.save(
        new UserAsk(
            ask.id(),
            ask.receiverUserId(),
            ask.askerUserId(),
            ask.content(),
            ask.isAnonymous(),
            ask.anonymousNicknameId(),
            ask.anonymousProfileImageId(),
            true,
            ask.createdAt(),
            ask.updatedAt()));
  }

  private AnonymousIdentity assignAnonymousIdentity(Long receiverId) {
    List<Long> excludeIds =
        userAskRepositoryPort.findDistinctAnonymousNicknameIdsByReceiverUserId(receiverId);
    List<AnonymousNickname> excludeNicknames =
        excludeIds.stream().map(id -> new AnonymousNickname(id, null)).toList();

    AnonymousNickname nickname =
        anonymousNicknameRetriever.findRandomAnonymousNickname(excludeNicknames);
    AnonymousProfileImage profileImage = anonymousProfileImageRetriever.getAnonymousProfileImage();

    return new AnonymousIdentity(nickname.id(), profileImage.id());
  }

  private void validateAskOwner(UserAsk ask, Long userId) {
    if (ask.askerUserId() == null || !ask.askerUserId().equals(userId)) {
      throw new UserAskException(UserAskFailure.UNAUTHORIZED_ASK_UPDATE);
    }
  }

  private void validateAnswerOwner(UserAsk ask, Long userId, UserAskFailure failure) {
    if (!Objects.equals(ask.receiverUserId(), userId)) {
      throw new UserAskException(failure);
    }
  }

  private void validateNotAnswered(Long askId, UserAskFailure failure) {
    if (userAnswerRepositoryPort.existsByQuestionId(askId)) {
      throw new UserAskException(failure);
    }
  }

  private UserAsk getAskOrThrow(Long askId) {
    return userAskRepositoryPort
        .findById(askId)
        .orElseThrow(() -> new UserAskException(UserAskFailure.NOT_FOUND_ASK));
  }

  private UserAnswer getAnswerOrThrow(Long answerId) {
    return userAnswerRepositoryPort
        .findById(answerId)
        .orElseThrow(() -> new UserAskException(UserAskFailure.NOT_FOUND_ANSWER));
  }

  private record AnonymousIdentity(Long nicknameId, Long profileImageId) {}
}
