package org.sopt.makers.domain.app.soptletter.port;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.app.soptletter.SoptLetter;

public interface SoptLetterRepositoryPort {

  Optional<SoptLetter> findById(Long letterId);

  List<SoptLetter> findPageByTopicId(Long topicId, Long cursor, int size);

  Optional<SoptLetter> findLatestByTopicId(Long topicId);

  long countByTopicId(Long topicId);

  long countByAuthorProfileIdSince(Long authorProfileId, LocalDateTime since);

  boolean existsByIdAndTopicId(Long letterId, Long topicId);

  SoptLetter save(SoptLetter soptLetter);

  void updateMessage(Long letterId, String message, LocalDateTime updatedAt);

  void deleteById(Long letterId);

  void increaseLikeCount(Long letterId);

  void decreaseLikeCount(Long letterId);
}
