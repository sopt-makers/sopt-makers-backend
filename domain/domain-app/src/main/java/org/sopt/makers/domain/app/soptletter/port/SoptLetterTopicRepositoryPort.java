package org.sopt.makers.domain.app.soptletter.port;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.soptletter.SoptLetterTopic;

public interface SoptLetterTopicRepositoryPort {

  Optional<SoptLetterTopic> findById(Long topicId);

  List<SoptLetterTopic> findAllLatestFirst();

  List<SoptLetterTopic> findDefaultTopicsLatestFirst();

  List<SoptLetterTopic> findNormalTopicsLatestFirst();

  boolean existsNormalTopic();

  List<SoptLetterTopic> findActiveCtasLatestFirst(LocalDateTime now);
}
