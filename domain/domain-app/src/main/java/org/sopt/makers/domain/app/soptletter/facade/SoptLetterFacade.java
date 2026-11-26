package org.sopt.makers.domain.app.soptletter.facade;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptletter.SoptLetterPage;
import org.sopt.makers.domain.app.soptletter.SoptLetterProfile;
import org.sopt.makers.domain.app.soptletter.SoptLetterTopic;
import org.sopt.makers.domain.app.soptletter.SoptLetterTopicReadEvent;
import org.sopt.makers.domain.app.soptletter.SoptLetterTopicView;
import org.sopt.makers.domain.app.soptletter.SoptLetterView;
import org.sopt.makers.domain.app.soptletter.service.SoptLetterProfileService;
import org.sopt.makers.domain.app.soptletter.service.SoptLetterService;
import org.sopt.makers.domain.app.soptletter.service.SoptLetterTopicService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SoptLetterFacade {

  private final SoptLetterService soptLetterService;
  private final SoptLetterProfileService soptLetterProfileService;
  private final SoptLetterTopicService soptLetterTopicService;
  private final ApplicationEventPublisher eventPublisher;

  public boolean isOnboarded(Long userId) {
    return soptLetterProfileService.isOnboarded(userId);
  }

  public SoptLetterProfile getOrCreateProfile(Long userId) {
    return soptLetterProfileService.getOrCreate(userId);
  }

  public SoptLetterProfile completeOnboarding(Long userId) {
    return soptLetterProfileService.completeOnboarding(userId);
  }

  public String getReportFormUrl() {
    return soptLetterService.getReportFormUrl();
  }

  public Optional<SoptLetterTopic> findActiveCta() {
    return soptLetterTopicService.findActiveCta();
  }

  public List<SoptLetterTopicView> getTopics(Long userId, String type) {
    LocalDateTime now = soptLetterTopicService.now();
    List<SoptLetterTopic> topics = soptLetterTopicService.getTopics(type, now);
    Set<Long> hotTopicIds = soptLetterService.getHotTopicIds(now);
    Map<Long, Long> unreadCounts = soptLetterService.countUnreadByTopic(userId);
    return topics.stream()
        .map(
            topic ->
                new SoptLetterTopicView(
                    topic,
                    topic.isNewAt(now),
                    hotTopicIds.contains(topic.id()),
                    unreadCounts.getOrDefault(topic.id(), 0L)))
        .toList();
  }

  public SoptLetterTopic getTopic(Long topicId) {
    return soptLetterTopicService.getById(topicId);
  }

  public LocalDateTime now() {
    return soptLetterTopicService.now();
  }

  public SoptLetterPage getTopicMessages(Long userId, Long topicId, Long cursor, int size) {
    SoptLetterProfile requester = soptLetterProfileService.getByUserId(userId);
    SoptLetterTopic topic = soptLetterTopicService.getById(topicId);
    SoptLetterPage page = soptLetterService.getMessages(requester, topic, cursor, size, null);
    publishTopicRead(userId, cursor, page);
    return page;
  }

  public SoptLetterPage getDefaultTopicMessages(Long userId, Long cursor, int size) {
    SoptLetterProfile requester = soptLetterProfileService.getByUserId(userId);
    SoptLetterTopic topic = soptLetterTopicService.getLatestDefaultTopic();
    Boolean hasNormalTopic = soptLetterTopicService.existsNormalTopic();
    SoptLetterPage page =
        soptLetterService.getMessages(requester, topic, cursor, size, hasNormalTopic);
    publishTopicRead(userId, cursor, page);
    return page;
  }

  public SoptLetterView getMessage(Long userId, Long topicId, Long messageId) {
    SoptLetterProfile requester = soptLetterProfileService.getByUserId(userId);
    return soptLetterService.getMessage(requester, topicId, messageId);
  }

  @Transactional
  public SoptLetterView createMessage(Long userId, Long topicId, String content) {
    SoptLetterProfile author = soptLetterProfileService.getByUserId(userId);
    SoptLetterTopic topic = soptLetterTopicService.getById(topicId);
    return soptLetterService.create(author, topic, content);
  }

  @Transactional
  public SoptLetterView updateMessage(Long userId, Long topicId, Long messageId, String content) {
    SoptLetterProfile author = soptLetterProfileService.getByUserId(userId);
    return soptLetterService.update(author, topicId, messageId, content);
  }

  @Transactional
  public void deleteMessage(Long userId, Long topicId, Long messageId) {
    SoptLetterProfile author = soptLetterProfileService.getByUserId(userId);
    soptLetterService.delete(author, topicId, messageId);
  }

  public void addLike(Long userId, Long topicId, Long messageId) {
    soptLetterService.addLike(userId, topicId, messageId);
  }

  public void removeLike(Long userId, Long topicId, Long messageId) {
    soptLetterService.removeLike(userId, topicId, messageId);
  }

  private void publishTopicRead(Long userId, Long cursor, SoptLetterPage page) {
    if (cursor != null || page.messages().isEmpty()) {
      return;
    }
    eventPublisher.publishEvent(
        new SoptLetterTopicReadEvent(
            userId, page.topic().id(), page.messages().getFirst().letter().id()));
  }
}
