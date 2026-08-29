package org.sopt.makers.domain.crew.slack.port;

import java.util.List;
import org.sopt.makers.domain.crew.slack.SlackEmojiMapping;

public interface SlackEmojiMappingRepositoryPort {

  boolean existsByCallEmojiAndUserSlackId(String callEmoji, String userSlackId);

  List<SlackEmojiMapping> findAllByCallEmoji(String callEmoji);

  List<SlackEmojiMapping> findAllByCallEmojiForUpdate(String callEmoji);

  SlackEmojiMapping save(SlackEmojiMapping mapping);

  void updateCallEmoji(String originalCallEmoji, String updatedCallEmoji);

  void deleteAllByCallEmoji(String callEmoji);
}
