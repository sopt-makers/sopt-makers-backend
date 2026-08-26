package org.sopt.makers.domain.app.soptletter.facade;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.app.soptletter.SoptLetterPage;
import org.sopt.makers.domain.app.soptletter.SoptLetterProfile;
import org.sopt.makers.domain.app.soptletter.SoptLetterTopic;
import org.sopt.makers.domain.app.soptletter.SoptLetterView;
import org.sopt.makers.domain.app.soptletter.service.SoptLetterProfileService;
import org.sopt.makers.domain.app.soptletter.service.SoptLetterService;
import org.sopt.makers.domain.app.soptletter.service.SoptLetterTopicService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SoptLetterFacade {

  private final SoptLetterService soptLetterService;
  private final SoptLetterProfileService soptLetterProfileService;
  private final SoptLetterTopicService soptLetterTopicService;

  public boolean isOnboarded(Long userId) {
    return soptLetterProfileService.isOnboarded(userId);
  }

  @Transactional
  public SoptLetterProfile getOrCreateProfile(Long userId) {
    return soptLetterProfileService.getOrCreate(userId);
  }

  @Transactional
  public SoptLetterProfile completeOnboarding(Long userId) {
    return soptLetterProfileService.completeOnboarding(userId);
  }

  public String getReportFormUrl() {
    return soptLetterService.getReportFormUrl();
  }

  public Optional<SoptLetterTopic> findActiveCta() {
    return soptLetterTopicService.findActiveCta();
  }

  public List<SoptLetterTopic> getTopics(String type) {
    return soptLetterTopicService.getTopics(type);
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
    return soptLetterService.getMessages(requester, topic, cursor, size, null);
  }

  public SoptLetterPage getDefaultTopicMessages(Long userId, Long cursor, int size) {
    SoptLetterProfile requester = soptLetterProfileService.getByUserId(userId);
    SoptLetterTopic topic = soptLetterTopicService.getLatestDefaultTopic();
    Boolean hasNormalTopic = soptLetterTopicService.existsNormalTopic();
    return soptLetterService.getMessages(requester, topic, cursor, size, hasNormalTopic);
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
}
