package org.sopt.makers.domain.app.soptletter.service;

import static org.sopt.makers.core.constant.AsyncExecutorConstant.CACHE_SYNC_EXECUTOR;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptletter.SoptLetterTopicReadEvent;
import org.sopt.makers.domain.app.soptletter.port.SoptLetterTopicReadRepositoryPort;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SoptLetterTopicReadEventListener {

  private final SoptLetterTopicReadRepositoryPort soptLetterTopicReadRepositoryPort;

  @Async(CACHE_SYNC_EXECUTOR)
  @EventListener
  public void handle(SoptLetterTopicReadEvent event) {
    soptLetterTopicReadRepositoryPort.upsertLastReadLetterId(
        event.userId(), event.topicId(), event.lastReadLetterId());
  }
}
