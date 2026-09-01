package org.sopt.makers.storage.db.crew.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.meeting.Member;
import org.sopt.makers.domain.crew.meeting.MemberRole;
import org.sopt.makers.domain.crew.meeting.port.MemberRepositoryPort;
import org.sopt.makers.storage.db.crew.entity.MeetingMemberEntity;
import org.sopt.makers.storage.db.crew.repository.MeetingMemberJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberRepositoryAdapter implements MemberRepositoryPort {

  private final MeetingMemberJpaRepository meetingMemberJpaRepository;

  @Transactional
  @Override
  public Member save(Member member) {
    return meetingMemberJpaRepository.save(MeetingMemberEntity.fromDomain(member)).toDomain();
  }

  @Transactional
  @Override
  public Member saveOrReplaceRole(Member member) {
    return meetingMemberJpaRepository
        .findByMeetingIdAndUserId(member.meetingId(), member.userId())
        .map(
            entity -> {
              entity.replaceRole(member.role());
              return entity.toDomain();
            })
        .orElseGet(
            () ->
                meetingMemberJpaRepository.save(MeetingMemberEntity.fromDomain(member)).toDomain());
  }

  @Override
  public List<Member> findAllByMeetingId(Long meetingId) {
    return meetingMemberJpaRepository.findAllByMeetingId(meetingId).stream()
        .map(MeetingMemberEntity::toDomain)
        .toList();
  }

  @Override
  public Optional<Member> findByMeetingIdAndUserId(Long meetingId, Long userId) {
    return meetingMemberJpaRepository
        .findByMeetingIdAndUserId(meetingId, userId)
        .map(MeetingMemberEntity::toDomain);
  }

  @Override
  public List<Member> findAllByMeetingIdsAndRole(List<Long> meetingIds, MemberRole role) {
    if (meetingIds == null || meetingIds.isEmpty()) {
      return List.of();
    }
    return meetingMemberJpaRepository.findAllByMeetingIdInAndRole(meetingIds, role).stream()
        .map(MeetingMemberEntity::toDomain)
        .toList();
  }

  @Override
  public List<Member> findAllByMeetingIdsAndUserId(List<Long> meetingIds, Long userId) {
    if (meetingIds == null || meetingIds.isEmpty()) {
      return List.of();
    }
    return meetingMemberJpaRepository.findAllByMeetingIdInAndUserId(meetingIds, userId).stream()
        .map(MeetingMemberEntity::toDomain)
        .toList();
  }

  @Override
  public long countByMeetingIdAndRole(Long meetingId, MemberRole role) {
    return meetingMemberJpaRepository.countByMeetingIdAndRole(meetingId, role);
  }

  @Transactional
  @Override
  public void deleteByMeetingIdAndUserIdAndRole(Long meetingId, Long userId, MemberRole role) {
    meetingMemberJpaRepository.deleteByMeetingIdAndUserIdAndRole(meetingId, userId, role);
  }

  @Transactional
  @Override
  public void deleteAllByMeetingId(Long meetingId) {
    meetingMemberJpaRepository.deleteAllByMeetingId(meetingId);
  }
}
