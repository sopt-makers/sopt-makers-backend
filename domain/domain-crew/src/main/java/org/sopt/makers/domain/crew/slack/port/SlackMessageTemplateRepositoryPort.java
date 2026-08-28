package org.sopt.makers.domain.crew.slack.port;

import java.util.Optional;
import org.sopt.makers.domain.crew.slack.SlackMessageTemplate;

public interface SlackMessageTemplateRepositoryPort {

  Optional<SlackMessageTemplate> findByTemplateCode(String templateCode);
}
