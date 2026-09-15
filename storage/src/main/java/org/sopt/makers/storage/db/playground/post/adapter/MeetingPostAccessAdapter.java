package org.sopt.makers.storage.db.playground.post.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.post.MeetingPostContext;
import org.sopt.makers.domain.playground.post.PostMeetingImage;
import org.sopt.makers.domain.playground.post.port.MeetingPostAccessPort;
import org.sopt.makers.storage.db.crew.entity.MeetingEntity;
import org.sopt.makers.storage.db.crew.entity.MeetingMemberEntity;
import org.sopt.makers.storage.db.crew.repository.MeetingJpaRepository;
import org.sopt.makers.storage.db.crew.repository.MeetingMemberJpaRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MeetingPostAccessAdapter implements MeetingPostAccessPort {

  private final MeetingJpaRepository meetingRepository;
  private final MeetingMemberJpaRepository memberRepository;

  @Override
  public Optional<MeetingPostContext> findMeeting(Long meetingId, Long userId) {
    return meetingRepository
        .findById(meetingId)
        .map(
            meeting ->
                toContext(
                    meeting,
                    userId != null
                        && memberRepository
                            .findByMeetingIdAndUserId(meetingId, userId)
                            .isPresent()));
  }

  @Override
  public List<MeetingPostContext> findMeetingsByUserId(Long userId) {
    List<Long> meetingIds =
        memberRepository.findAllByUserId(userId).stream()
            .map(MeetingMemberEntity::getMeetingId)
            .distinct()
            .toList();
    return meetingRepository.findAllById(meetingIds).stream()
        .map(meeting -> toContext(meeting, true))
        .toList();
  }

  @Override
  public List<Long> findMemberIds(Long meetingId) {
    return memberRepository.findAllByMeetingId(meetingId).stream()
        .map(MeetingMemberEntity::getUserId)
        .toList();
  }

  private MeetingPostContext toContext(MeetingEntity meeting, boolean member) {
    List<PostMeetingImage> images =
        meeting.getImages() == null
            ? List.of()
            : meeting.getImages().stream()
                .map(image -> new PostMeetingImage(image.id(), image.url()))
                .toList();
    return new MeetingPostContext(
        meeting.getId(),
        meeting.getTitle(),
        meeting.getCategory().getValue(),
        images,
        meeting.getDescription(),
        member);
  }
}
