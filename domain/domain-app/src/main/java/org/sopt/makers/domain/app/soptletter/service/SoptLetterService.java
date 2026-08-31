package org.sopt.makers.domain.app.soptletter.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.operationconfig.OperationConfigCategory;
import org.sopt.makers.domain.app.operationconfig.service.OperationConfigService;
import org.sopt.makers.domain.app.soptletter.SoptLetter;
import org.sopt.makers.domain.app.soptletter.SoptLetterColor;
import org.sopt.makers.domain.app.soptletter.SoptLetterPage;
import org.sopt.makers.domain.app.soptletter.SoptLetterProfile;
import org.sopt.makers.domain.app.soptletter.SoptLetterTopic;
import org.sopt.makers.domain.app.soptletter.SoptLetterView;
import org.sopt.makers.domain.app.soptletter.exception.SoptLetterException;
import org.sopt.makers.domain.app.soptletter.exception.SoptLetterFailure;
import org.sopt.makers.domain.app.soptletter.port.SoptLetterLikeRepositoryPort;
import org.sopt.makers.domain.app.soptletter.port.SoptLetterProfileRepositoryPort;
import org.sopt.makers.domain.app.soptletter.port.SoptLetterRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoptLetterService {

  private static final int DAILY_MESSAGE_LIMIT = 10;
  private static final String REPORT_FORM_URL_KEY = "linkUrl";

  private final SoptLetterRepositoryPort soptLetterRepositoryPort;
  private final SoptLetterLikeRepositoryPort soptLetterLikeRepositoryPort;
  private final SoptLetterProfileRepositoryPort soptLetterProfileRepositoryPort;
  private final SoptLetterGenerator soptLetterGenerator;
  private final OperationConfigService operationConfigService;
  private final Clock clock;

  public String getReportFormUrl() {
    return operationConfigService.getValue(
        OperationConfigCategory.SOPT_LETTER, REPORT_FORM_URL_KEY);
  }

  public SoptLetterPage getMessages(
      SoptLetterProfile requester,
      SoptLetterTopic topic,
      Long cursor,
      int size,
      Boolean hasNormalTopic) {
    validatePageSize(size);
    return assemblePage(requester, topic, cursor, size, hasNormalTopic);
  }

  public SoptLetterView getMessage(SoptLetterProfile requester, Long topicId, Long letterId) {
    SoptLetter letter = getLetter(letterId);
    letter.validateInTopic(topicId);

    boolean mine = letter.isAuthor(requester.id());
    return new SoptLetterView(
        letter,
        mine ? requester.nickname() : getAuthorNickname(letter.authorProfileId()),
        soptLetterLikeRepositoryPort.existsByLetterIdAndUserId(letterId, requester.userId()),
        mine);
  }

  @Transactional
  public SoptLetterView create(SoptLetterProfile author, SoptLetterTopic topic, String message) {
    validateDailyLimit(author.id());

    SoptLetterColor previousColor =
        soptLetterRepositoryPort
            .findLatestByTopicId(topic.id())
            .map(SoptLetter::color)
            .orElse(null);
    SoptLetter saved =
        soptLetterRepositoryPort.save(
            soptLetterGenerator.generate(author.id(), topic.id(), message, previousColor));

    return new SoptLetterView(saved, author.nickname(), false, true);
  }

  @Transactional
  public SoptLetterView update(
      SoptLetterProfile author, Long topicId, Long letterId, String message) {
    SoptLetter letter = getLetter(letterId);
    letter.validateInTopic(topicId);
    letter.validateAuthor(author.id());
    LocalDateTime updatedAt = LocalDateTime.now(clock);
    soptLetterRepositoryPort.updateMessage(letterId, message, updatedAt);

    return new SoptLetterView(
        letter.withMessage(message, updatedAt),
        author.nickname(),
        soptLetterLikeRepositoryPort.existsByLetterIdAndUserId(letterId, author.userId()),
        true);
  }

  @Transactional
  public void delete(SoptLetterProfile author, Long topicId, Long letterId) {
    SoptLetter letter = getLetter(letterId);
    letter.validateInTopic(topicId);
    letter.validateAuthor(author.id());

    soptLetterLikeRepositoryPort.deleteAllByLetterId(letterId);
    soptLetterRepositoryPort.deleteById(letterId);
  }

  @Transactional
  public void addLike(Long userId, Long topicId, Long letterId) {
    validateLetterInTopic(topicId, letterId);
    if (soptLetterLikeRepositoryPort.addIfAbsent(userId, letterId) > 0) {
      soptLetterRepositoryPort.increaseLikeCount(letterId);
    }
  }

  @Transactional
  public void removeLike(Long userId, Long topicId, Long letterId) {
    validateLetterInTopic(topicId, letterId);
    if (soptLetterLikeRepositoryPort.deleteByLetterIdAndUserId(letterId, userId) > 0) {
      soptLetterRepositoryPort.decreaseLikeCount(letterId);
    }
  }

  private SoptLetterPage assemblePage(
      SoptLetterProfile requester,
      SoptLetterTopic topic,
      Long cursor,
      int size,
      Boolean hasNormalTopic) {
    List<SoptLetter> fetched =
        soptLetterRepositoryPort.findPageByTopicId(topic.id(), cursor, size + 1);
    boolean hasNext = fetched.size() > size;
    List<SoptLetter> letters = hasNext ? fetched.subList(0, size) : fetched;

    Set<Long> likedLetterIds =
        soptLetterLikeRepositoryPort.findLikedLetterIds(
            requester.userId(), ids(letters, SoptLetter::id));
    Map<Long, String> nicknamesByProfileId = getAuthorNicknames(letters);

    List<SoptLetterView> views =
        letters.stream()
            .map(
                letter ->
                    new SoptLetterView(
                        letter,
                        nicknamesByProfileId.get(letter.authorProfileId()),
                        likedLetterIds.contains(letter.id()),
                        letter.isAuthor(requester.id())))
            .toList();

    return new SoptLetterPage(
        topic,
        soptLetterRepositoryPort.countByTopicId(topic.id()),
        letters.isEmpty() ? null : letters.getLast().id(),
        hasNext,
        hasNormalTopic,
        views);
  }

  private Map<Long, String> getAuthorNicknames(List<SoptLetter> letters) {
    if (letters.isEmpty()) {
      return Map.of();
    }
    Set<Long> authorProfileIds = ids(letters, SoptLetter::authorProfileId);
    Map<Long, String> nicknamesByProfileId =
        soptLetterProfileRepositoryPort.findAllByIds(authorProfileIds).stream()
            .collect(Collectors.toMap(SoptLetterProfile::id, SoptLetterProfile::nickname));
    if (nicknamesByProfileId.size() != authorProfileIds.size()) {
      throw new SoptLetterException(SoptLetterFailure.NOT_FOUND_SOPT_LETTER_PROFILE);
    }
    return nicknamesByProfileId;
  }

  private String getAuthorNickname(Long authorProfileId) {
    return soptLetterProfileRepositoryPort
        .findById(authorProfileId)
        .map(SoptLetterProfile::nickname)
        .orElseThrow(
            () -> new SoptLetterException(SoptLetterFailure.NOT_FOUND_SOPT_LETTER_PROFILE));
  }

  private SoptLetter getLetter(Long letterId) {
    return soptLetterRepositoryPort
        .findById(letterId)
        .orElseThrow(() -> new SoptLetterException(SoptLetterFailure.NOT_FOUND_SOPT_LETTER));
  }

  private void validateLetterInTopic(Long topicId, Long letterId) {
    if (!soptLetterRepositoryPort.existsByIdAndTopicId(letterId, topicId)) {
      throw new SoptLetterException(SoptLetterFailure.NOT_FOUND_SOPT_LETTER);
    }
  }

  private void validateDailyLimit(Long authorProfileId) {
    LocalDateTime startOfDay = LocalDateTime.now(clock).toLocalDate().atStartOfDay();
    if (soptLetterRepositoryPort.countByAuthorProfileIdSince(authorProfileId, startOfDay)
        >= DAILY_MESSAGE_LIMIT) {
      throw new SoptLetterException(SoptLetterFailure.DAILY_MESSAGE_LIMIT_EXCEEDED);
    }
  }

  private void validatePageSize(int size) {
    if (size <= 0) {
      throw new SoptLetterException(SoptLetterFailure.INVALID_PAGE_SIZE);
    }
  }

  private Set<Long> ids(List<SoptLetter> letters, Function<SoptLetter, Long> extractor) {
    return letters.stream().map(extractor).collect(Collectors.toSet());
  }
}
