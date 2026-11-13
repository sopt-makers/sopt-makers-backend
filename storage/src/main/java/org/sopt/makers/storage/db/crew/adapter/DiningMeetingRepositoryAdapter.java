package org.sopt.makers.storage.db.crew.adapter;

import java.time.LocalDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.dining.exception.*;
import org.sopt.makers.domain.crew.dining.model.DiningMeeting;
import org.sopt.makers.domain.crew.dining.port.DiningMeetingPort;
import org.sopt.makers.domain.crew.meeting.*;
import org.sopt.makers.storage.db.crew.entity.*;
import org.sopt.makers.storage.db.crew.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiningMeetingRepositoryAdapter implements DiningMeetingPort {
  private final MeetingJpaRepository meetings;
  private final MeetingMemberJpaRepository members;

  @Override
  @Transactional
  public DiningMeeting save(DiningMeeting dining) {
    if (dining.getId() != null) {
      var entity =
          meetings
              .findById(dining.getId())
              .orElseThrow(() -> new DiningException(DiningFailure.NOT_FOUND_DINING));
      entity.updateDining(dining);
      return toDomain(entity);
    }
    var meeting =
        Meeting.create(
            null,
            dining.getTitle(),
            null,
            MeetingCategory.DINING,
            dining.getImageURL(),
            dining.getStartDate(),
            dining.getEndDate(),
            0,
            "",
            "",
            dining.getmStartDate(),
            dining.getmEndDate(),
            "",
            "",
            false,
            false,
            null,
            dining.getCreatedGeneration(),
            null,
            Arrays.asList(dining.getJoinableParts()));
    var entity = meetings.save(MeetingEntity.fromDomain(meeting));
    members.save(
        MeetingMemberEntity.fromDomain(
            new Member(entity.getId(), dining.getUserId(), MemberRole.LEADER)));
    return toDomain(entity);
  }

  @Override
  public Optional<DiningMeeting> findById(Long id) {
    return meetings
        .findById(id)
        .filter(m -> m.getCategory() == MeetingCategory.DINING)
        .map(this::toDomain);
  }

  @Override
  public Optional<DiningMeeting> findByIdForUpdate(Long id) {
    return meetings
        .findByIdForUpdate(id)
        .filter(m -> m.getCategory() == MeetingCategory.DINING)
        .map(this::toDomain);
  }

  @Override
  public List<DiningMeeting> findOngoingDiningsByDateBetween(
      MeetingCategory category, LocalDateTime start, LocalDateTime end) {
    return toDomains(
        meetings.findAll(
            (root, query, cb) ->
                cb.and(
                    cb.equal(root.get("category"), category),
                    cb.isNull(root.get("diningEndedAt")),
                    cb.greaterThanOrEqualTo(root.get("activityStartDate"), start),
                    cb.lessThan(root.get("activityStartDate"), end))));
  }

  @Override
  @Transactional
  public void delete(DiningMeeting value) {
    members.deleteAllByMeetingId(value.getId());
    meetings.deleteById(value.getId());
  }

  public DiningMeeting toDomain(MeetingEntity entity) {
    Long leader =
        members.findAllByMeetingId(entity.getId()).stream()
            .filter(m -> m.getRole() == MemberRole.LEADER)
            .map(MeetingMemberEntity::getUserId)
            .findFirst()
            .orElseThrow(() -> new DiningException(DiningFailure.NOT_FOUND_USER));
    return toDomain(entity, leader);
  }

  public List<DiningMeeting> toDomains(List<MeetingEntity> entities) {
    if (entities.isEmpty()) return List.of();
    var leaders =
        members
            .findAllByMeetingIdInAndRole(
                entities.stream().map(MeetingEntity::getId).toList(), MemberRole.LEADER)
            .stream()
            .collect(
                java.util.stream.Collectors.toMap(
                    MeetingMemberEntity::getMeetingId, MeetingMemberEntity::getUserId));
    return entities.stream().map(entity -> toDomain(entity, leaders.get(entity.getId()))).toList();
  }

  private DiningMeeting toDomain(MeetingEntity entity, Long leader) {
    if (leader == null) throw new DiningException(DiningFailure.NOT_FOUND_USER);
    return DiningMeeting.builder()
        .id(entity.getId())
        .userId(leader)
        .title(entity.getTitle())
        .imageURL(entity.getImages())
        .startDate(entity.getStartDate())
        .endDate(entity.getEndDate())
        .mStartDate(entity.getActivityStartDate())
        .mEndDate(entity.getActivityEndDate())
        .joinableParts(entity.getJoinableParts().toArray(MeetingJoinablePart[]::new))
        .createdGeneration(entity.getCreatedGeneration())
        .diningEndedAt(entity.getDiningEndedAt())
        .isDateModified(entity.isDateModified())
        .isJoinablePartsModified(entity.isJoinablePartsModified())
        .createdTimestamp(entity.getCreatedAt())
        .build();
  }
}
