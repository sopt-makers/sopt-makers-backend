package org.sopt.makers.domain.app.soptletter.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptletter.SoptLetterTopic;
import org.sopt.makers.domain.app.soptletter.exception.SoptLetterException;
import org.sopt.makers.domain.app.soptletter.exception.SoptLetterFailure;
import org.sopt.makers.domain.app.soptletter.port.SoptLetterTopicRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoptLetterTopicService {

  private static final String TYPE_DEFAULT = "default";
  private static final String TYPE_NORMAL = "normal";

  private final SoptLetterTopicRepositoryPort soptLetterTopicRepositoryPort;
  private final Clock clock;

  public SoptLetterTopic getById(Long topicId) {
    return soptLetterTopicRepositoryPort
        .findById(topicId)
        .orElseThrow(() -> new SoptLetterException(SoptLetterFailure.NOT_FOUND_SOPT_LETTER_TOPIC));
  }

  public List<SoptLetterTopic> getTopics(String type) {
    if (type == null) {
      return soptLetterTopicRepositoryPort.findAllLatestFirst();
    }
    if (TYPE_DEFAULT.equalsIgnoreCase(type)) {
      return soptLetterTopicRepositoryPort.findDefaultTopicsLatestFirst();
    }
    if (TYPE_NORMAL.equalsIgnoreCase(type)) {
      return soptLetterTopicRepositoryPort.findNormalTopicsLatestFirst();
    }
    throw new SoptLetterException(SoptLetterFailure.INVALID_TOPIC_TYPE);
  }

  public SoptLetterTopic getLatestDefaultTopic() {
    return soptLetterTopicRepositoryPort.findDefaultTopicsLatestFirst().stream()
        .findFirst()
        .orElseThrow(() -> new SoptLetterException(SoptLetterFailure.NOT_FOUND_SOPT_LETTER_TOPIC));
  }

  public boolean existsNormalTopic() {
    return soptLetterTopicRepositoryPort.existsNormalTopic();
  }

  public LocalDateTime now() {
    return LocalDateTime.now(clock);
  }

  public Optional<SoptLetterTopic> findActiveCta() {
    return soptLetterTopicRepositoryPort
        .findActiveCtasLatestFirst(LocalDateTime.now(clock))
        .stream()
        .findFirst();
  }
}
