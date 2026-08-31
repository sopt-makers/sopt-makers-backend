package org.sopt.makers.storage.db.crew.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.meeting.MeetingCategory;
import org.sopt.makers.domain.official.homepage.port.HomepageStudyCountPort;
import org.sopt.makers.storage.db.crew.repository.MeetingJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomepageStudyCountAdapter implements HomepageStudyCountPort {

  private final MeetingJpaRepository meetingJpaRepository;

  @Override
  public int getStudyCountByGeneration(Integer generationId) {
    return meetingJpaRepository.countAllByCreatedGenerationAndCategory(
        generationId, MeetingCategory.STUDY);
  }
}
