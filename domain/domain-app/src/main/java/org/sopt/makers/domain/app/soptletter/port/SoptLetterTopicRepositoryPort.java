package org.sopt.makers.domain.app.soptletter.port;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.soptletter.SoptLetterTopic;

public interface SoptLetterTopicRepositoryPort {

  Optional<SoptLetterTopic> findById(Long topicId);

  List<SoptLetterTopic> findActiveTopicsDefaultFirst(LocalDateTime now);

  List<SoptLetterTopic> findActiveDefaultTopicsLatestFirst(LocalDateTime now);

  List<SoptLetterTopic> findActiveNormalTopicsLatestFirst(LocalDateTime now);

  List<SoptLetterTopic> findDefaultTopicsLatestFirst();

  boolean existsNormalTopic();

  List<SoptLetterTopic> findActiveCtasLatestFirst(LocalDateTime now);
}
