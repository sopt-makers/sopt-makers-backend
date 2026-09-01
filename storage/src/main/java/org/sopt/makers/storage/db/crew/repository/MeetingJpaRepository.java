package org.sopt.makers.storage.db.crew.repository;

import java.util.Optional;
import org.sopt.makers.domain.crew.meeting.MeetingCategory;
import org.sopt.makers.domain.crew.meeting.MemberRole;
import org.sopt.makers.storage.db.crew.entity.MeetingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetingJpaRepository
    extends JpaRepository<MeetingEntity, Long>, JpaSpecificationExecutor<MeetingEntity> {

  int countAllByCreatedGenerationAndCategory(Integer createdGeneration, MeetingCategory category);

  Optional<MeetingEntity> findFirstByTitleOrderByIdDesc(String title);

  Optional<MeetingEntity> findFirstByTitleContainingOrderByIdDesc(String title);

  @Query(
      "SELECT meeting FROM MeetingEntity meeting "
          + "JOIN MeetingMemberEntity member ON member.meetingId = meeting.id "
          + "WHERE member.userId = :userId AND member.role = :role")
  Page<MeetingEntity> findAllByMember(
      @Param("userId") Long userId, @Param("role") MemberRole role, Pageable pageable);

  default Page<MeetingEntity> findAllByLeaderUserId(Long userId, Pageable pageable) {
    return findAllByMember(userId, MemberRole.LEADER, pageable);
  }

  @Query(
      "SELECT meeting FROM MeetingEntity meeting "
          + "JOIN MeetingMemberEntity member ON member.meetingId = meeting.id "
          + "WHERE member.userId = :userId")
  Page<MeetingEntity> findAllByMemberUserId(@Param("userId") Long userId, Pageable pageable);

  Page<MeetingEntity> findAllByMeetingDemandId(Long meetingDemandId, Pageable pageable);

  long countByMeetingDemandId(Long meetingDemandId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      "UPDATE MeetingEntity meeting SET meeting.meetingDemandId = NULL WHERE meeting.meetingDemandId = :meetingDemandId")
  void clearMeetingDemandId(@Param("meetingDemandId") Long meetingDemandId);
}
