package org.sopt.makers.domain.crew.flash.service;

import static org.sopt.makers.domain.crew.flash.exception.FlashFailure.NOT_FOUND_FLASH;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.flash.Flash;
import org.sopt.makers.domain.crew.flash.exception.FlashException;
import org.sopt.makers.domain.crew.flash.port.FlashRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FlashService {

  private final FlashRepositoryPort flashRepositoryPort;

  @Transactional
  public Flash createFlash(
      Long leaderUserId, Long meetingId, Flash.UpdateValues values, Integer createdGeneration) {
    return flashRepositoryPort.save(
        Flash.create(leaderUserId, meetingId, values, createdGeneration));
  }

  @Transactional
  public Flash updateFlash(Long meetingId, Long userId, Flash.UpdateValues values) {
    Flash flash = getByMeetingId(meetingId);
    flash.validateLeader(userId);
    return flashRepositoryPort.save(flash.update(values));
  }

  public Flash getByMeetingId(Long meetingId) {
    return flashRepositoryPort
        .findByMeetingId(meetingId)
        .orElseThrow(() -> new FlashException(NOT_FOUND_FLASH));
  }

  @Transactional
  public void deleteByMeetingIdIfExists(Long meetingId) {
    flashRepositoryPort.deleteByMeetingId(meetingId);
  }
}
